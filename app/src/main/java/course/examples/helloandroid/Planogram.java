package course.examples.helloandroid;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.sql.*;

//TODO: Stripping text is what takes more time on device. For a better performance, it would be
//maybe necessary to have a desktop application that does this operation and create a xml? file
//as input for this class.


public class Planogram {

    public static final String COMP_ACRONYM = "PGM";

    private String mDepartmentNb;
    private String mDepartmentName;
    private String mDepartmentNameNorm;
    private String mPlanoLength;
    private String mPlanoCreationShortDate;

    private Date mPlanoCreationDate;

    private int mNbProducts;

    private ArrayList<Product> mProducts;

    private boolean[] mIsPlaced;
    private boolean[] mIsNew;

    private ProductDBHandler mProductDB;

    private File mPdfFile;
    private String mPdfText = null;

    public Planogram() {
    }

    public Planogram(File pdfFile) {

        mPdfFile = pdfFile;

        stripPDF();

        findTitleInText();
        findDateInText();
        findProductsInText();

        // Array for indicating if the product has been placed correctly
        mIsPlaced = new boolean[mNbProducts];
        Arrays.fill(mIsPlaced, false);

        // Array for indicating if product is new and not in the inventory
        mIsNew = new boolean[mNbProducts];
        Arrays.fill(mIsNew, false);
    }

    public void save(Context context, String databaseName){

        mProductDB = new ProductDBHandler(context,databaseName);
        mProductDB.create();

        for(int i = 0; i < mNbProducts; i++) {
            mProductDB.addProduct(mProducts.get(i));
        }
    }

    public void open(Context context, String databaseName, String databasePath){
        mProductDB = new ProductDBHandler(context,databaseName);
        mProductDB.open(databasePath);

        mNbProducts = mProductDB.getNbProducts();

        mProducts = new ArrayList<>(mNbProducts);
        mProducts = mProductDB.getAllProducts();

        mIsPlaced = new boolean[mNbProducts];
        mIsNew = new boolean[mNbProducts];

        for(int i = 0; i < mNbProducts; i++) {
            mIsPlaced[i] = mProducts.get(i).isPlaced();
            mIsNew[i] = mProducts.get(i).isNew();
        }
    }

    private void stripPDF() {
        String pdfString = null;

        Log.d("Planogram","Stripping text...");

        // Put the PDF in a string
        try {
            PDDocument document = null;
            document = PDDocument.load(mPdfFile);
            document.getClass();
            if( !document.isEncrypted() ){
                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition( true );
                PDFTextStripper Tstripper = new PDFTextStripper();
                mPdfText = Tstripper.getText(document);

                document.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        Log.d("Planogram","Finished stripping text");
    }

    private void findTitleInText() {

        // Pattern to match the document title
        String docTitlePattern = "Rapport par tablette\\n([0-9]{4}) - (.*)\\n([0-9]{1,2})\\s[P,p]ieds";
        Pattern docTitlePatternObj = Pattern.compile(docTitlePattern);
        Matcher docTitleMatcher = docTitlePatternObj.matcher(mPdfText);

        if (docTitleMatcher.find()) {
            mDepartmentNb = docTitleMatcher.group(1);
            mDepartmentName = docTitleMatcher.group(2);
            mPlanoLength = docTitleMatcher.group(3);

            mDepartmentNameNorm = mDepartmentName.replaceAll(" ", "_");
            mDepartmentNameNorm = Normalizer.normalize(mDepartmentNameNorm, Normalizer.Form.NFD);
            mDepartmentNameNorm = mDepartmentNameNorm.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        }
    }

    private void findDateInText() {

        //Matches date
        String datePattern = "([0-9]{1,2})\\s(\\w{3,})\\s([0-9]{4})";
        Pattern datePatternObject = Pattern.compile(datePattern);
        Matcher dateMatcher = datePatternObject.matcher(mPdfText);

        if (dateMatcher.find()) {
            try {
                SimpleDateFormat dateFormatInput = new SimpleDateFormat("dd-MMMM-yyyy", Locale.FRENCH);
                mPlanoCreationDate = dateFormatInput.parse(dateMatcher.group(1)
                        + "-" + dateMatcher.group(2) + "-" + dateMatcher.group(3));
                SimpleDateFormat dateFormatOutput = new SimpleDateFormat("ddMMyy");
                mPlanoCreationShortDate = dateFormatOutput.format(mPlanoCreationDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void findProductsInText() {

        int i = 0;

        int shelfNumber = 0;
        int shelfHeight = 0;

        // Pattern to match the products lines
        String prodLinePattern = "([0-9]{1,3})\\s([0-9]{6})\\s([0-9]{12})\\s(\\S+)\\s(.*)\\s([0-9]+\\sX\\s[0-9]+\\s\\w+)\\s([0-9])";
        Pattern prodPatternObject = Pattern.compile(prodLinePattern);

        // Count number of products... "There's got to be a better way!"
        Matcher nbProductsMatcher = prodPatternObject.matcher(mPdfText);
        mNbProducts = 0;
        while (nbProductsMatcher.find())
            mNbProducts++;

        mProducts = new ArrayList<>(mNbProducts);

        String[] lines = mPdfText.split(System.getProperty("line.separator"));

        Log.d("Planogram","Finding products...");

        // Main loop to parse the string from the PDF
        for (String currentLine: lines) {

            //Matches shelves number and height
            String shelfPattern = "Tablette ([0-9]{1,2}).*Hauteur = ([0-9]{1,2})";
            Pattern shelfPatternObject = Pattern.compile(shelfPattern);
            Matcher shelfMatcher = shelfPatternObject.matcher(currentLine);

            if (shelfMatcher.find()) {

                shelfNumber = Integer.parseInt(shelfMatcher.group(1));
                shelfHeight = Integer.parseInt(shelfMatcher.group(2));
            }

            // Matches product
            Matcher prodMatcher = prodPatternObject.matcher(currentLine);
            if (prodMatcher.find()) {

                mProducts.add(new Product(i,prodMatcher.group(2),
                        prodMatcher.group(3),
                        prodMatcher.group(5),
                        prodMatcher.group(6),
                        Integer.parseInt(prodMatcher.group(7)),
                        shelfNumber,shelfHeight,false));

                i++;

            }

        }
        // End of main loop

        Log.d("Planogram","Finished finding products");
    }

    /**
     * Find product
     *
     * Returns position of the first product found or 0 if it's not found
     *
     **/
    public int findProduct() {

        int prodPosition;

        prodPosition = 0;

        return prodPosition;
    }

    /**
     * Insert product into planogram
     *
     * If product is not found it will be marked as "removed"
     *
     **/
    public void insertProduct() {

    }

    public void setExpirationAtPos(int position, Expiration exp){

        mProducts.get(position).setExpiration(exp);
        mProductDB.updateProduct(mProducts.get(position));
    }

    public Product getProduct(int position){

        return mProducts.get(position);
    }

    public int getNbProducts(){

        return mNbProducts;
    }

    public String getDepartmentNb(){
        return mDepartmentNb;
    }

    public String getDepartmentName(){
        return mDepartmentName;
    }

    public String getDepartmentNameNorm(){
        return mDepartmentNameNorm;
    }

    public String getPlanoLength(){
        return mPlanoLength;
    }

    public Date getPlanoCreationDate(){
        return mPlanoCreationDate;
    }

    public String getPlanoCreationShortDate(){
        return mPlanoCreationShortDate;
    }

    public void productIsPlaced(int pos) {
        mIsPlaced[pos] = true;
        mProductDB.updateProduct(mProducts.get(pos).getUpc(),ProductDBHandler.KEY_ISPLACED,"1");
    }

    public boolean isProductPlaced(int pos) {
        return mIsPlaced[pos];
    }

    public void productIsNew(int pos) {
        mIsNew[pos] = true;
        mProductDB.updateProduct(mProducts.get(pos).getUpc(),ProductDBHandler.KEY_ISNEWPROD,"1");
    }

    public boolean isProductNew(int pos) {
        return mIsNew[pos];
    }

}