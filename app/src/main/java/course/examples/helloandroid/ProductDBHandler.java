package course.examples.helloandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ProductDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private final String TABLE_NAME;

    public static final String KEY_ID = "id";
    public static final String KEY_POS = "position";
    public static final String KEY_IDNB = "McKIdNb";
    public static final String KEY_UPC = "UPC";
    public static final String KEY_DESC = "description";
    public static final String KEY_FORMAT = "format";
    public static final String KEY_NBFACING = "nbFacing";
    public static final String KEY_SHELFNB = "shelfNb";
    public static final String KEY_SHELFHGT = "shelfHeight";
    public static final String KEY_EXPIRATION = "expiration";
    public static final String KEY_ISNEWPROD = "isNewProd";
    public static final String KEY_ISPLACED = "isPlaced";

    private static final String DIRECTION_ASC = "ASC";
    private static final String DIRECTION_DESC = "DESC";

    private static final String[] COLUMNS = {KEY_ID, KEY_POS,KEY_IDNB,KEY_UPC,
            KEY_DESC,KEY_FORMAT,KEY_NBFACING,KEY_SHELFNB,KEY_SHELFHGT,
            KEY_EXPIRATION,KEY_ISNEWPROD,KEY_ISPLACED};

    private int mId = 1;
    private String mOrder;

    private SQLiteDatabase mProductDB = null;

    public ProductDBHandler(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);

        TABLE_NAME = databaseName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_PRODUCT_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                KEY_ID + " INT PRIMARY KEY," +
                KEY_POS + " INT," +
                KEY_IDNB + " CHAR(6)," +
                KEY_UPC + " CHAR(12) NOT NULL UNIQUE," +
                KEY_DESC + " TEXT NOT NULL," +
                KEY_FORMAT + " TEXT NOT NULL," +
                KEY_NBFACING + " INT," +
                KEY_SHELFNB + " INT," +
                KEY_SHELFHGT + " INT," +
                KEY_EXPIRATION + " CHAR(6)," +
                KEY_ISNEWPROD + " INT DEFAULT 0," +
                KEY_ISPLACED + " INT DEFAULT 0)";

        db.execSQL(CREATE_PRODUCT_TABLE);

        sort(KEY_ID,DIRECTION_ASC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public void create() {
        mProductDB = this.getWritableDatabase();
    }

    public void open(String databasePath) {
        mProductDB = SQLiteDatabase.openDatabase(databasePath + TABLE_NAME, null, 0);
    }

    public void close() {
        mProductDB.close();
    }

    public boolean isOpen() {
        return mProductDB.isOpen();
    }

    public void addProduct(Product product){
        //Log.d("addProduct", product.toString());
        SQLiteDatabase db;
        // 1. get reference to writable DB
        if(mProductDB == null) {
            db = this.getWritableDatabase();
        } else {
            db = mProductDB;
        }

        // 2. create ContentValues to add key "column"/value
        ContentValues values = putValues(product);

        // 3. insert
        db.insert(TABLE_NAME, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        mId++;
    }

    public void insertProduct(Product product) {
        //Log.d("insertProduct", product.toString());
        SQLiteDatabase db;
        // 1. get reference to writable DB
        if(mProductDB == null) {
            db = this.getWritableDatabase();
        } else {
            db = mProductDB;
        }

        // 2. create ContentValues to add key "column"/value
        ContentValues values = putValues(product);

        String INCREMENT_POS = "UPDATE " + TABLE_NAME + " SET " +
                KEY_POS + " = " + KEY_POS + " + 1 WHERE " + KEY_POS + " >= " + product.getPos();

        db.execSQL(INCREMENT_POS);

        // 3. insert
        db.insert(TABLE_NAME, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();

        mId++;
    }

    public Product getProduct(int pos){

        SQLiteDatabase db;
        // 1. get reference to writable DB
        if(mProductDB == null) {
            db = this.getWritableDatabase();
        } else {
            db = mProductDB;
        }

        // 2. build query
        Cursor cursor =
                db.query(TABLE_NAME, // a. table
                        COLUMNS, // b. column names
                        " " + KEY_POS + " = ?", // c. selections
                        new String[] { String.valueOf(pos) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        mOrder, // g. order by
                        null); // h. limit


        Product product;
        // 3. if we got results get the first one
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();

            product = createProduct(cursor);

            Log.d("getProduct(" + pos + ")", product.toString());

            cursor.close();
        } else {
            product = null;
        }

        return product;
    }

    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> products = new ArrayList<>();

        // 1. build the query
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + mOrder;

        // 2. get reference to writable DB
        SQLiteDatabase db;
        if(mProductDB == null) {
            db = this.getWritableDatabase();
        } else {
            db = mProductDB;
        }
        Cursor cursor = db.rawQuery(query, null);

        Product product;
        if (cursor.moveToFirst()) {
            do {
                product = createProduct(cursor);

                products.add(product);
            } while (cursor.moveToNext());
        }

        //Log.d("getAllProducts()", products.toString());

        cursor.close();

        return products;
    }

    public ArrayList<Product> findProduct(String column, String operator, String value) {

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        if(operator.equals("contains")) {
            operator = " LIKE \'%";
            value = value + "%\'";
        } else {
            value = "\'" + value + "\'";
        }

        switch(column){
            case "Position":
                column = KEY_POS;
                break;
            case "McKesson Id":
                column = KEY_IDNB;
                break;
            case "UPC":
                column = KEY_UPC;
                break;
            case "Description":
                column = KEY_DESC;
                break;
        }

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE "
                + column + operator + value + " ORDER BY " + mOrder;

        // 2. build query
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Product> products = new ArrayList<>();
        // 3. if we got results get the first one
        Product product;
        if (cursor.moveToFirst()) {
            do {
                product = createProduct(cursor);

                products.add(product);
            } while (cursor.moveToNext());
        } else {
            products.add(null);
        }

        return products;
    }

    public int updateProduct(String upc, String attrToChange, String newValue) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        if (attrToChange.equals(KEY_POS)) {
            Log.d("updateProduct","To be implemented... maybe");
            return 0;
        }

        ContentValues args = new ContentValues();
        args.put(attrToChange, newValue);

        // 3. updating row
        int i = db.update(TABLE_NAME, //table
                args, // column/value
                KEY_UPC + " = ?", // selections
                new String[] { upc }); //selection args

        // 4. close
        db.close();

        return i;
    }

    public void deleteProduct(Product product) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_NAME,
                KEY_POS + " = ?",
                new String[] { String.valueOf(product.getPos()) });

        String DECREMENT_POS = "UPDATE " + TABLE_NAME + " SET " +
                KEY_POS + " = " + KEY_POS + " - 1 WHERE " + KEY_POS + " >= " + product.getPos();

        db.execSQL(DECREMENT_POS);

        // 3. close
        db.close();

        //Log.d("deleteProduct", product.toString());

    }

    private ContentValues putValues(Product product) {
        ContentValues values = new ContentValues();

        values.put(KEY_ID, mId);
        values.put(KEY_POS, product.getPos());
        values.put(KEY_IDNB, product.getIdNb());
        values.put(KEY_UPC, product.getUpc());
        values.put(KEY_DESC, product.getDesc());
        values.put(KEY_FORMAT, product.getFormat());
        values.put(KEY_NBFACING, product.getNbFacing());
        values.put(KEY_SHELFNB, product.getShelfNb());
        values.put(KEY_SHELFHGT, product.getShelfHeight());

        if(product.isExpired()) {
            values.put(KEY_EXPIRATION,product.getExpiration().getExpCode());
        } else {
            values.put(KEY_EXPIRATION,"");
        }

        values.put(KEY_ISNEWPROD, product.isNew() ? 1 : 0);
        values.put(KEY_ISPLACED, product.isPlaced() ? 1 : 0);

        //Log.d("putValues",values.toString());

        return values;
    }

    private Product createProduct(Cursor cursor) {
        Product product = new Product();

        product.setPos(cursor.getInt(1));
        product.setIdNb(cursor.getString(2));
        product.setUpc(cursor.getString(3));
        product.setDesc(cursor.getString(4));
        product.setFormat(cursor.getString(5));
        product.setNbFacing(cursor.getInt(6));
        product.setShelfNb(cursor.getInt(7));
        product.setShelfHeight(cursor.getInt(8));

        product.setExpiration(new Expiration(cursor.getString(9)));
        product.setIsNewProd((cursor.getInt(10) == 1));
        product.setIsPlaced((cursor.getInt(11) == 1));

        return product;
    }

    public int getNbProducts() {

        SQLiteDatabase db;

        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        if(mProductDB == null) {
            db = this.getWritableDatabase();
        } else {
            db = mProductDB;
        }

        Cursor cursor = db.rawQuery(countQuery, null);

        int nbProducts = cursor.getCount();
        cursor.close();

        return nbProducts;
    }

    public int getId(int pos) {
        SQLiteDatabase db;
        // 1. get reference to writable DB
        if(mProductDB == null) {
            db = this.getWritableDatabase();
        } else {
            db = mProductDB;
        }

        // 2. build query
        Cursor cursor =
                db.query(TABLE_NAME, // a. table
                        new String[] {KEY_ID}, // b. column names
                        " " + KEY_POS + " = ?", // c. selections
                        new String[] { String.valueOf(pos) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null) {
            cursor.moveToFirst();

            Log.d("getId",cursor.getString(0));
            return cursor.getInt(0);
        } else {
            return 0;
        }
    }

    public void sort(String column, String direction){
        mOrder = column + " " + direction;
    }


}
