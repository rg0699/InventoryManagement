package com.example.inventorymanagement.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.inventorymanagement.R;
import com.example.inventorymanagement.database.InventoryContract.ProductEntry;
import com.example.inventorymanagement.database.InventoryDbHelper;
import com.example.inventorymanagement.models.Product;
import com.example.inventorymanagement.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_PHOTO = 100;

    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    ImageView productImage;
    ImageButton newPhotoButton;
    ImageButton chooseFromGalleryButton;
    EditText productNameEditText;
    EditText productQuantityEditText;
    EditText productPriceEditText;
    TextView supplierNameEditText;
    TextView supplierPhoneEditText;

    Bitmap imageBitmap;
    private boolean isProductEdited = false;
    private String what;
    private InventoryDbHelper productDbhelper;
    private SQLiteDatabase db;
    private long productId;
    private Spinner categorySpinner;
    private ArrayList<String> categories;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabase;
    private StorageReference storageRef;
    private ValueEventListener listener;
    private Bitmap bitmap;
    private Button PhotoButton;
    private Uri outputFileUri;
    private Uri mImageUri;

    public static byte[] getBytes(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            return stream.toByteArray();
        } else {
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();

        productNameEditText = findViewById(R.id.edit_product_name);
        productQuantityEditText = findViewById(R.id.edit_quantity);
        productPriceEditText = findViewById(R.id.edit_price);
        supplierNameEditText = findViewById(R.id.edit_supplier_name);
        supplierPhoneEditText = findViewById(R.id.edit_supplier_phone);
        newPhotoButton = findViewById(R.id.new_photo_button);
        chooseFromGalleryButton = findViewById(R.id.choose_from_gallery_button);
        productImage = findViewById(R.id.product_image);

        categorySpinner = findViewById(R.id.categorySpinner);
        categories = new ArrayList<>();
        categories.addAll(MainActivity.categoriesList);
        categories.add("Other");
        categorySpinner.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1, categories));

        productDbhelper = new InventoryDbHelper(getApplicationContext());
        db = productDbhelper.getReadableDatabase();

        what = getIntent().getStringExtra("what");

        assert what != null;
        if (what.equals("add")) {
            setTitle("Add Product");
        } else {
            setTitle("Edit Product");
            productId = getIntent().getLongExtra("id",0);
            getProductDetails();
        }

        newPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        });
        chooseFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_PHOTO);
            }
        });

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    showMessage("User data is null!");
                }
                else {
                    supplierNameEditText.setText(user.name);
                    supplierPhoneEditText.setText(user.phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showMessage("Failed to read user!"
                        + " Please try again later");
            }
        };

    }


    private void getProductDetails() {
        Product product = productDbhelper.getProduct(productId);

        productNameEditText.setText(product.getProduct_name());
        productQuantityEditText.setText(String.valueOf(product.getProduct_qauntity()));
        productPriceEditText.setText(String.valueOf(product.getProduct_price()));
        supplierNameEditText.setText(product.getSupplier_name());
        supplierPhoneEditText.setText(product.getSupplier_phone());

        bitmap = product.getProduct_image();
        if(bitmap != null){
            productImage.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (resultCode == RESULT_OK) {
                    imageBitmap = (Bitmap) data.getExtras().get("data");
                    productImage.setImageBitmap(imageBitmap);
                }
                break;
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK && data != null) {
                    Uri imageUri = data.getData();
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    } catch (IOException ie) {
                        ie.printStackTrace();
                    }
                    productImage.setImageBitmap(imageBitmap);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                String productNameString = productNameEditText.getText().toString().trim();
                String quantityString = productQuantityEditText.getText().toString().trim();
                String priceString = productPriceEditText.getText().toString().trim();
                getBytes(imageBitmap);

                if (TextUtils.isEmpty(productNameString) || TextUtils.isEmpty(priceString) || TextUtils.isEmpty(quantityString)) {
                    Toast.makeText(this, getString(R.string.empty_field_toast), Toast.LENGTH_LONG).show();
                } else {
                    saveProduct();
                    finish();
                }
                return true;
            case android.R.id.home:
                if (!isProductEdited) {
                    finish();
//                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener buttonClick =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };
                unsavedDataDialog(buttonClick);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!isProductEdited) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener buttonClick =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                };
        unsavedDataDialog(buttonClick);
    }

    private void unsavedDataDialog(DialogInterface.OnClickListener buttonClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard Changes");
        builder.setPositiveButton("Yes", buttonClick);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveProduct() {
        String stringProductName = productNameEditText.getText().toString().trim();
        String stringQuantity = productQuantityEditText.getText().toString().trim();
        String stringPrice = productPriceEditText.getText().toString().trim();
        String stringSupplierName = supplierNameEditText.getText().toString().trim();
        String stringSupplierPhone = supplierPhoneEditText.getText().toString().trim();
        byte[] imageByte = getBytes(imageBitmap);
        String category = categories.get(categorySpinner.getSelectedItemPosition());

        ContentValues values = new ContentValues();
        values.put(ProductEntry.PRODUCT_NAME, stringProductName);
        values.put(ProductEntry.PRODUCT_CATEGORY, category);
        values.put(ProductEntry.QUANTITY, stringQuantity);
        values.put(ProductEntry.PRICE, stringPrice);
        values.put(ProductEntry.SUPPLIER_NAME, stringSupplierName);
        values.put(ProductEntry.SUPPLIER_PHONE, stringSupplierPhone);
        if (imageByte != null) {
            values.put(ProductEntry.IMAGE, imageByte);
        }

        if(what.equals("add")){

            Boolean b = productDbhelper.insert(values);
            if (!b) {
                Toast.makeText(getApplicationContext(), "Failed to add inventory!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Inventory Added Successfully!", Toast.LENGTH_SHORT).show();
            }

        }
        else {
            Boolean b = productDbhelper.update(values , productId);
            if (!b) {
                Toast.makeText(getApplicationContext(), "Failed to update inventory!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Inventory Updated Successfully!", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void showMessage(String s){
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase.child("users").child(mCurrentUser.getUid()).addListenerForSingleValueEvent(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener != null){
            mDatabase.child("users").child(mCurrentUser.getUid()).removeEventListener(listener);
        }
    }
}