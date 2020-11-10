package com.example.inventorymanagement.database;

import android.provider.BaseColumns;

public final class InventoryContract {


    private InventoryContract() {
    }

    public static final class ProductEntry implements BaseColumns {

        public static final String TABLE_NAME = "product";
        public static final String PRODUCT_ID = "id";
        public static final String PRODUCT_CATEGORY = "category";
        public static final String PRODUCT_NAME = "name";
        public static final String QUANTITY = "quantity";
        public static final String PRICE = "price";
        public static final String SUPPLIER_NAME = "supplier_name";
        public static final String SUPPLIER_PHONE = "supplier_phone";
        public static final String IMAGE = "image";
    }
}