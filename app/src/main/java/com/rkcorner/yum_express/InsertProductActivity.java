package com.rkcorner.yum_express;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class InsertProductActivity extends AppCompatActivity {

    private EditText productNameEditText;
    private EditText productPriceEditText;
    private EditText productQuantityEditText;
    private ImageView selectedImageView;
    private Button selectImageButton;
    private Button insertProductButton;
    private DatabaseHelper databaseHelper;
    private byte[] imageByteArray;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_product);

        productNameEditText = findViewById(R.id.et_product_name);
        productPriceEditText = findViewById(R.id.et_product_price);
        productQuantityEditText = findViewById(R.id.et_product_quantity);
        selectedImageView = findViewById(R.id.iv_selected_image);
        selectImageButton = findViewById(R.id.btn_select_image);
        insertProductButton = findViewById(R.id.btn_insert_product);

        databaseHelper = new DatabaseHelper(this);

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    selectedImageView.setImageBitmap(imageBitmap);
                    imageByteArray = bitmapToByteArray(imageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        selectImageButton.setOnClickListener(view -> showImageSelectionDialog());

        insertProductButton.setOnClickListener(view -> insertProduct());
    }

    private void showImageSelectionDialog() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        imagePickerLauncher.launch(pickIntent);
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void insertProduct() {
        String name = productNameEditText.getText().toString();
        String priceStr = productPriceEditText.getText().toString();
        String quantityStr = productQuantityEditText.getText().toString();

        if (name.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty() || imageByteArray == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        int quantity = Integer.parseInt(quantityStr);

        databaseHelper.insertProduct(name, price, quantity, imageByteArray);
        Toast.makeText(this, "Product inserted successfully", Toast.LENGTH_SHORT).show();

        // Reset input fields and image view
        productNameEditText.setText("");
        productPriceEditText.setText("");
        productQuantityEditText.setText("");
        selectedImageView.setImageResource(R.drawable.upload_icon);
        imageByteArray = null;
    }
}