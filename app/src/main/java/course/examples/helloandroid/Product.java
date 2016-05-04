package course.examples.helloandroid;

/**
 * Implements a product
 *
 */

public class Product {

    boolean isRemoved; //Okay goes in there, it's discontinued
    boolean isNewProd; //Hmm...

    int nbFacing;
    int shelfNb;
    int shelfHeight;

    String idNb;
    String upc;
    String description;
    String format;
    Expiration exp;

    public Product() {
    }

    public Product(String idNb, String upc, String description, String format,
                   int nbFacing, int shelfNb, int shelfHeight, boolean isNewProd) {

        this.idNb = idNb;
        this.upc = upc;
        this.description = description;
        this.format = format;
        this.nbFacing = nbFacing;
        this.shelfNb = shelfNb;
        this.shelfHeight = shelfHeight;
        this.isNewProd = isNewProd;
    }

    public Product(String idNb, String upc, String description, String format,
                   int nbFacing, int shelfNb, int shelfHeight, boolean isNewProd, Expiration exp) {

        this.idNb = idNb;
        this.upc = upc;
        this.description = description;
        this.format = format;
        this.nbFacing = nbFacing;
        this.shelfNb = shelfNb;
        this.shelfHeight = shelfHeight;
        this.isNewProd = isNewProd;
        this.exp = exp;
    }

    public Product(String idNb, String upc, String description, String format, int nbFacing) {

        this.idNb = idNb;
        this.upc = upc;
        this.description = description;
        this.format = format;
        this.nbFacing = nbFacing;

        this.isNewProd = false;
    }

    // Set product attributes
    public void setIdNb(String idNb) {
        this.idNb = idNb;
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
        this.exp = exp;
    }

    // get product attributes
    public String getIdNb() {
        return idNb;
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

    public boolean getIsNewProd() {
        return isNewProd;
    }

    public Expiration getExpiration() {
        return exp;
    }

    public boolean isExpired(){

        if(exp == null)
            return false;
        else
            return true;
    }

    public String getProductInfo() {
        String temp;

        temp = idNb + " " + upc + " " + description + " " + format + " " + nbFacing;

        return temp;
    }

    public void printProductInfo() {

        System.out.println(idNb + " " + upc + " " + description + " " + format + " " + nbFacing);

    }

}