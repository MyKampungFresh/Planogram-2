package course.examples.helloandroid;

/**
 * Implements a product
 *
 * A product is identified by its McKesson ID number, its UPC, its description and its format.
 * It can be expiring soon or be a new product. Along with those attributes, a product is usually
 * localised in a store at one or more emplacement. A localisation is a unambiguous section in a store,
 * habitually 4 feet of length, with shelves and or hooks to place products. In the localisation, a
 * product has a position defined by its order from the top left, in ascending order from left to
 * right, top to bottom. The position is given to the first from the left if there are multiple
 * contiguous copies of the same product. The number of copies is the number of facing. If the product
 * is on two or more shelves separated by other products (i.e. not contiguous), it has then another
 * position along with the other attributes (number of facing, shelf number and shelf height).
 *
 */

public class Product {

    private boolean isNewProd = false;
    private boolean mIsPlaced = false;

    //TODO Change those attributes in arrays
    private int mPos;
    private int nbFacing;
    private int shelfNb;
    private int shelfHeight;

    String mMcKessonId;
    String upc;
    String description;
    String format;
    Expiration mExp = null;

    public Product() {
    }

    public Product(String idNb, String upc, String description, String format) {
        mMcKessonId = idNb;
        this.upc = upc;
        this.description = description;
        this.format = format;
    }

    public Product(int pos, String idNb, String upc, String description, String format,
                   int nbFacing, int shelfNb, int shelfHeight, boolean isNewProd) {

        mPos = pos;
        mMcKessonId = idNb;
        this.upc = upc;
        this.description = description;
        this.format = format;
        this.nbFacing = nbFacing;
        this.shelfNb = shelfNb;
        this.shelfHeight = shelfHeight;
        this.isNewProd = isNewProd;
    }

    public Product(int pos, String idNb, String upc, String description, String format,
                   int nbFacing, int shelfNb, int shelfHeight, boolean isNewProd, Expiration exp) {

        mPos = pos;
        mMcKessonId = idNb;
        this.upc = upc;
        this.description = description;
        this.format = format;
        this.nbFacing = nbFacing;
        this.shelfNb = shelfNb;
        this.shelfHeight = shelfHeight;
        this.isNewProd = isNewProd;
        mExp = exp;
    }

    public Product(int pos, String idNb, String upc, String description, String format, int nbFacing) {

        mPos = pos;
        mMcKessonId = idNb;
        this.upc = upc;
        this.description = description;
        this.format = format;
        this.nbFacing = nbFacing;

        this.isNewProd = false;
    }

    // Set product attributes
    public void setIsPlaced(boolean isPlaced) {
        mIsPlaced = isPlaced;
    }

    public void setPos(int pos) {
        mPos = pos;
    }

    public void setIdNb(String idNb) {
        mMcKessonId = idNb;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public void setDesc(String description) {
        this.description = description;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setNbFacing(int nbFacing) {
        this.nbFacing = nbFacing;
    }

    public void setShelfNb(int shelfNb) {
        this.shelfNb = shelfNb;
    }

    public void setShelfHeight(int shelfHeight) {
        this.shelfHeight = shelfHeight;
    }

    public void setIsNewProd(boolean isNewProd) {
        this.isNewProd = isNewProd;
    }

    public void setExpiration(Expiration exp) {
        mExp = exp;
    }

    // get product attributes
    public boolean isPlaced() {
        return mIsPlaced;
    }

    public int getPos() {
        return mPos;
    }

    public String getIdNb() {
        return mMcKessonId;
    }

    public String getUpc() {
        return upc;
    }

    public String getDesc() {
        return description;
    }

    public String getFormat() {
        return format;
    }

    public int getNbFacing() {
        return nbFacing;
    }

    public int getShelfNb() {
        return shelfNb;
    }

    public int getShelfHeight() {
        return shelfHeight;
    }

    public boolean isNew() {
        return isNewProd;
    }

    public Expiration getExpiration() {
        return mExp;
    }

    public boolean isExpired(){

        if(mExp.isValid())
            return true;
        else
            return false;
    }

    public String toString() {
        return mPos + " " + mMcKessonId + " " + upc + " " + description + " " + format + " " + nbFacing;
    }

}