package com.example.inventorymanagement.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    private long product_id;
    private String product_name;
    private String product_category;
    private Bitmap product_image;
    private int product_qauntity;
    private int product_price;
    private String supplier_name;
    private String supplier_phone;
    private String supplier_id;

    public Product() {

    }

    public Product(long product_id, String product_name, int product_qauntity, int product_price,
                   Bitmap product_image, String supplier_name, String supplier_phone) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_qauntity = product_qauntity;
        this.product_image = product_image;
        this.product_price = product_price;
        this.supplier_name = supplier_name;
        this.supplier_phone = supplier_phone;
    }

    public Product(String name, int product_qauntity, int product_price, Bitmap product_image) {
        this.product_name = name;
        this.product_qauntity = product_qauntity;
        this.product_price = product_price;
        this.product_image = product_image;
    }

    public long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(long id) {
        this.product_id = id;
    }

    public String getProduct_category() {
        return product_category;
    }

    public void setProduct_category(String name) {
        this.product_category = name;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String name) {
        this.product_name = name;
    }

    public Bitmap getProduct_image() {
        return product_image;
    }

    public void setProduct_image(Bitmap name) {
        this.product_image = name;
    }

    public int getProduct_qauntity() {
        return product_qauntity;
    }

    public void setProduct_qauntity(int name) {
        this.product_qauntity = name;
    }

    public int getProduct_price() {
        return product_price;
    }

    public void setProduct_price(int name) {
        this.product_price = name;
    }

    public String getSupplier_name() {
        return supplier_name;
    }

    public void setSupplier_name(String name) {
        this.supplier_name = name;
    }

    public String getSupplier_phone() {
        return supplier_phone;
    }

    public void setSupplier_phone(String name) {
        this.supplier_phone = name;
    }


    public String getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(String name) {
        this.supplier_id = name;
    }


    public Product(Parcel in){
        String[] data = new String[7];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.product_id = Long.parseLong(data[0]);
        this.product_category = data[1];
        this.product_name = data[2];
        this.product_price = Integer.parseInt(data[3]);
        this.product_qauntity = Integer.parseInt(data[4]);
        //this.product_image = data[5];
        this.supplier_name = data[5];
        this.supplier_phone = data[6];
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {String.valueOf(this.product_id),
                this.product_category,
                this.product_name,
                String.valueOf(this.product_price),
                String.valueOf(this.product_qauntity),
                this.supplier_name,
                this.supplier_phone});
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

}
