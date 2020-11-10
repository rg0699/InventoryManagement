package com.example.inventorymanagement.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.inventorymanagement.database.InventoryContract.ProductEntry;
import com.example.inventorymanagement.models.Product;

import java.util.ArrayList;
import java.util.List;

public class InventoryDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";
    private Bitmap imageBitmap;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                        + ProductEntry.PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + ProductEntry.PRODUCT_CATEGORY + " TEXT NOT NULL, "
                        + ProductEntry.PRODUCT_NAME + " TEXT NOT NULL, "
                        + ProductEntry.QUANTITY + " INTEGER DEFAULT 0, "
                        + ProductEntry.PRICE + " INTEGER NOT NULL, "
                        + ProductEntry.IMAGE + " BLOB, "
                        + ProductEntry.SUPPLIER_NAME + " TEXT NOT NULL, "
                        + ProductEntry.SUPPLIER_PHONE + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME + ";");
        onCreate(db);
    }

    public List<Product> getAllProducts(String category)
    {
        List<Product> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from product WHERE category='" + category + "'";

        Cursor  res = db.rawQuery(query,null);
        //Cursor res =  db.rawQuery( "select * from "+ ProductEntry.TABLE_NAME, null );
        res.moveToFirst();

        if (res.moveToFirst()) {
            do {
                Product product = new Product();
                product.setProduct_id(res.getLong(res.getColumnIndex(ProductEntry.PRODUCT_ID)));
                product.setProduct_name(res.getString(res.getColumnIndex(ProductEntry.PRODUCT_NAME)));
                product.setProduct_category(res.getString(res.getColumnIndex(ProductEntry.PRODUCT_CATEGORY)));
                product.setProduct_price(res.getInt(res.getColumnIndex(ProductEntry.PRICE)));
                product.setProduct_qauntity(res.getInt(res.getColumnIndex(ProductEntry.QUANTITY)));
                product.setSupplier_name(res.getString(res.getColumnIndex(ProductEntry.SUPPLIER_NAME)));
                product.setSupplier_phone(res.getString(res.getColumnIndex(ProductEntry.SUPPLIER_PHONE)));

                byte[] imageByte = res.getBlob(res.getColumnIndex(ProductEntry.IMAGE));
                if (imageByte != null) {
                    imageBitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                    product.setProduct_image(imageBitmap);
                }

                arrayList.add(product);
            } while (res.moveToNext());
        }
        res.close();
        return arrayList;
    }

    public Boolean insert(ContentValues values) {

        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insert(ProductEntry.TABLE_NAME, null, values);
        return id != -1;
    }

    public Boolean delete(long productId) {

        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ProductEntry.TABLE_NAME, ProductEntry.PRODUCT_ID + "=" + productId, null) > 0;
    }

    public Boolean update(ContentValues values, long productId) {

        SQLiteDatabase db = this.getWritableDatabase();

        long id = db.update(ProductEntry.TABLE_NAME , values , ProductEntry.PRODUCT_ID + "=" + productId,null);
        return id != -1;
    }

    public Boolean checkIfExist(long productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select id from product WHERE id='" + productId + "'";
        Cursor cursor = db.rawQuery(sql,null);
        int tmp = cursor.getCount();
        cursor.close();
        return tmp > 0;
    }

    public Product getProduct(long productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from product WHERE id='" + productId + "'";
        Cursor res = db.rawQuery(sql,null);
        res.moveToFirst();
        Product product = new Product();
        product.setProduct_id(res.getLong(res.getColumnIndex(ProductEntry.PRODUCT_ID)));
        product.setProduct_name(res.getString(res.getColumnIndex(ProductEntry.PRODUCT_NAME)));
        product.setProduct_category(res.getString(res.getColumnIndex(ProductEntry.PRODUCT_CATEGORY)));
        product.setProduct_price(res.getInt(res.getColumnIndex(ProductEntry.PRICE)));
        product.setProduct_qauntity(res.getInt(res.getColumnIndex(ProductEntry.QUANTITY)));
        product.setSupplier_name(res.getString(res.getColumnIndex(ProductEntry.SUPPLIER_NAME)));
        product.setSupplier_phone(res.getString(res.getColumnIndex(ProductEntry.SUPPLIER_PHONE)));

        byte[] imageByte = res.getBlob(res.getColumnIndex(ProductEntry.IMAGE));
        if (imageByte != null) {
            imageBitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
            product.setProduct_image(imageBitmap);
        }
        res.close();
        return product;
    }
}

