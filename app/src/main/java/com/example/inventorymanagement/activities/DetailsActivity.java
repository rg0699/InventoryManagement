package com.example.inventorymanagement.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.inventorymanagement.R;
import com.example.inventorymanagement.database.InventoryDbHelper;
import com.example.inventorymanagement.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetailsActivity extends AppCompatActivity {

    TextView supplierPhoneTextView;
    TextView supplierNameTextView;
    TextView productNameTextView;
    TextView productPriceTextView;
    TextView productQuantityTextView;
    ImageView productImage;

    private Button callSupplierButton;
    private Bitmap bitmap;
    private long productId;
    private InventoryDbHelper productDbhelper;
    private SQLiteDatabase db;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_item_details);
        setTitle("Details");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null){
                    invalidateOptionsMenu();
                }
            }
        };

        productDbhelper = new InventoryDbHelper(getApplicationContext());
        db = productDbhelper.getReadableDatabase();

        productNameTextView = findViewById(R.id.details_product_name);
        productPriceTextView = findViewById(R.id.details_price);
        productQuantityTextView = findViewById(R.id.details_quantity);
        productImage = findViewById(R.id.details_image);
        supplierNameTextView = findViewById(R.id.details_supplier_name);
        supplierPhoneTextView = findViewById(R.id.details_supplier_phone);
        callSupplierButton = findViewById(R.id.call_button);

        callSupplierButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String phoneNumber = supplierPhoneTextView.getText().toString().trim();
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                supplierPhoneTextView.getText().toString().trim();
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                if (callIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(callIntent);
                }
            }
        });

        productId = getIntent().getLongExtra("id", 0);
        getProductDetails();
    }

    private void getProductDetails() {

        Product product = productDbhelper.getProduct(productId);

        productNameTextView.setText(product.getProduct_name());
        productQuantityTextView.setText(String.valueOf(product.getProduct_qauntity()));
        productPriceTextView.setText(String.valueOf(product.getProduct_price()));
        supplierNameTextView.setText(product.getSupplier_name());
        supplierPhoneTextView.setText(product.getSupplier_phone());

        bitmap = product.getProduct_image();
        if(bitmap != null){
            productImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_details, menu);
        if(mCurrentUser == null || !supplierPhoneTextView.getText().toString().equals(mCurrentUser.getPhoneNumber())){
            menu.findItem(R.id.edit_product).setVisible(false);
            menu.findItem(R.id.remove_product).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.remove_product:
                showDeleteConfirmationDialog();
                return true;
            case R.id.edit_product:
                Intent i = new Intent(DetailsActivity.this, EditActivity.class);
                i.putExtra("what", "edit");
                i.putExtra("id", productId);
                startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.remove_product));
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {

        Boolean b = productDbhelper.delete(productId);
        if (!b) {
            Toast.makeText(getApplicationContext(), "Failed to delete inventory!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Inventory Deleted Successfully!", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void showMessage(String s){
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authListener != null) {
            mAuth.removeAuthStateListener(authListener);
        }
    }
}
