package com.rkcorner.yum_express;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Test_DB";
    public static final int DATABASE_VERSION = 2;
    public static final String TABLE_REGISTER = "register";
    public static final String TABLE_PRODUCTS = "products";
    public static final String TABLE_CART = "cart";
    public static final String TABLE_ORDERS = "orders";

    public static final String COL_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_EMAIL = "email";
    public static final String COL_PASSWORD = "password";
    public static final String COL_MOBILE = "mobile";
    public static final String COL_PRODUCT_NAME = "productName";
    public static final String COL_PRODUCT_PRICE = "productPrice";
    public static final String COL_PRODUCT_QUANTITY = "productQuantity";
    public static final String COL_PRODUCT_IMAGE_URI = "productImageUri";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_REGISTER + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_PASSWORD + " TEXT, " +
                COL_MOBILE + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PRODUCT_NAME + " TEXT, " +
                COL_PRODUCT_PRICE + " REAL, " +
                COL_PRODUCT_QUANTITY + " INTEGER, " +
                COL_PRODUCT_IMAGE_URI + " BLOB)");

        db.execSQL("CREATE TABLE " + TABLE_CART + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PRODUCT_NAME + " TEXT, " +
                COL_PRODUCT_PRICE + " REAL, " +
                COL_PRODUCT_QUANTITY + " INTEGER, " +
                COL_PRODUCT_IMAGE_URI + " BLOB)");

        db.execSQL("CREATE TABLE " + TABLE_ORDERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PRODUCT_NAME + " TEXT, " +
                COL_PRODUCT_PRICE + " REAL, " +
                COL_PRODUCT_QUANTITY + " INTEGER, " +
                COL_PRODUCT_IMAGE_URI + " BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGISTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        onCreate(db);
    }

    public boolean insertUser(String username, String email, String password, String mobile) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USERNAME, username);
        contentValues.put(COL_EMAIL, email);
        contentValues.put(COL_PASSWORD, password);
        contentValues.put(COL_MOBILE, mobile);

        long result = db.insert(TABLE_REGISTER, null, contentValues);

        return result != -1;
    }

    public boolean checkUserByUsername(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_REGISTER + " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?", new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public void insertProduct(String name, double price, int quantity, byte[] imageByteArray) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PRODUCT_NAME, name);
        values.put(COL_PRODUCT_PRICE, price);
        values.put(COL_PRODUCT_QUANTITY, quantity);
        values.put(COL_PRODUCT_IMAGE_URI, imageByteArray);
        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }

    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);
    }

    public Cursor getProductByName(String productName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COL_PRODUCT_NAME + " = ?", new String[]{productName});
    }

    public void updateProduct(int productId, String productName, double price, int quantity, byte[] productImageByteArray) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_PRODUCT_NAME, productName);
        values.put(COL_PRODUCT_PRICE, price);
        values.put(COL_PRODUCT_QUANTITY, quantity);
        values.put(COL_PRODUCT_IMAGE_URI, productImageByteArray);

        db.update(TABLE_PRODUCTS, values, COL_ID + " = ?", new String[]{String.valueOf(productId)});
        db.close();
    }

    public boolean deleteProduct(String productName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_PRODUCTS, COL_PRODUCT_NAME + " = ?", new String[]{productName});
        db.close();
        return rowsAffected > 0;
    }

    public void addToCart(String productName, double productPrice, byte[] productImageUri, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CART + " WHERE " + COL_PRODUCT_NAME + " = ?", new String[]{productName});

            if (cursor.moveToFirst()) {
                int currentQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_QUANTITY));
                ContentValues values = new ContentValues();
                values.put(COL_PRODUCT_QUANTITY, currentQuantity + quantity);
                int rowsAffected = db.update(TABLE_CART, values, COL_PRODUCT_NAME + " = ?", new String[]{productName});
                if (rowsAffected > 0) {
                    Log.d("DatabaseHelper", "Updated cart for product: " + productName);
                } else {
                    Log.d("DatabaseHelper", "Failed to update cart for product: " + productName);
                }
            } else {
                ContentValues values = new ContentValues();
                values.put(COL_PRODUCT_NAME, productName);
                values.put(COL_PRODUCT_PRICE, productPrice);
                values.put(COL_PRODUCT_QUANTITY, quantity);
                values.put(COL_PRODUCT_IMAGE_URI, productImageUri);
                long newRowId = db.insert(TABLE_CART, null, values);
                if (newRowId != -1) {
                    Log.d("DatabaseHelper", "Inserted into cart for product: " + productName);
                } else {
                    Log.d("DatabaseHelper", "Failed to insert into cart for product: " + productName);
                }
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error in addToCart: " + e.getMessage());
        } finally {
            db.close();
        }
    }

    public void placeOrder(String productName, double productPrice, byte[] productImageUri, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ORDERS + " WHERE " + COL_PRODUCT_NAME + " = ?", new String[]{productName});

            if (cursor.moveToFirst()) {
                int currentQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_QUANTITY));
                ContentValues values = new ContentValues();
                values.put(COL_PRODUCT_QUANTITY, currentQuantity + quantity);
                int rowsAffected = db.update(TABLE_ORDERS, values, COL_PRODUCT_NAME + " = ?", new String[]{productName});
                if (rowsAffected > 0) {
                    Log.d("DatabaseHelper", "Updated order for product: " + productName);
                } else {
                    Log.d("DatabaseHelper", "Failed to update order for product: " + productName);
                }
            } else {
                ContentValues values = new ContentValues();
                values.put(COL_PRODUCT_NAME, productName);
                values.put(COL_PRODUCT_PRICE, productPrice);
                values.put(COL_PRODUCT_IMAGE_URI, productImageUri);
                values.put(COL_PRODUCT_QUANTITY, quantity);
                long newRowId = db.insert(TABLE_ORDERS, null, values);
                if (newRowId != -1) {
                    Log.d("DatabaseHelper", "Inserted into orders for product: " + productName);
                } else {
                    Log.d("DatabaseHelper", "Failed to insert into orders for product: " + productName);
                }
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error in placeOrder: " + e.getMessage());
        } finally {
            db.close();
        }
    }
}