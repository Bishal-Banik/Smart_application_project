package com.rkcorner.yum_express;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DeleteProductActivity extends AppCompatActivity {

    private EditText editTextName;
    private TextView textViewProductPrice;
    private TextView textViewProductQuantity;
    private TextView textViewProductId;
    private ImageView imageViewProduct;
    private Button buttonDelete;
    private Button buttonSearch;

    private DatabaseHelper databaseHelper;
    private byte[] productImageByteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_product);

        editTextName = findViewById(R.id.text_view_product_name);
        textViewProductPrice = findViewById(R.id.text_view_product_price);
        textViewProductQuantity = findViewById(R.id.text_view_product_quantity);
        textViewProductId = findViewById(R.id.text_view_product_id);
        imageViewProduct = findViewById(R.id.image_view_product);
        buttonDelete = findViewById(R.id.button_delete);
        buttonSearch = findViewById(R.id.button_search);

        databaseHelper = new DatabaseHelper(this);

        buttonSearch.setOnClickListener(view -> searchProduct());
        buttonDelete.setOnClickListener(view -> deleteProduct());
    }

    private void searchProduct() {
        String productName = editTextName.getText().toString().trim();
        if (productName.isEmpty()) {
            Toast.makeText(this, "Please enter a product name to search", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = databaseHelper.getProductByName(productName);
        if (cursor != null && cursor.moveToFirst()) {
            int productId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_PRICE));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_QUANTITY));
            byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_IMAGE_URI));

            textViewProductPrice.setText(String.valueOf(price));
            textViewProductQuantity.setText(String.valueOf(quantity));
            textViewProductId.setText("Product ID: " + productId);

            if (image != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                imageViewProduct.setImageBitmap(bitmap);
                productImageByteArray = image;
            } else {
                imageViewProduct.setImageResource(R.drawable.placeholder_image); // Placeholder if image is null
                productImageByteArray = null;
            }
            cursor.close();
        } else {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            clearProductDetails();
        }
    }

    private void clearProductDetails() {
        textViewProductPrice.setText("");
        textViewProductQuantity.setText("");
        textViewProductId.setText("");
        imageViewProduct.setImageResource(R.drawable.placeholder_image); // Placeholder image
        productImageByteArray = null;
    }

    private void deleteProduct() {
        String productName = editTextName.getText().toString().trim();
        if (productName.isEmpty()) {
            Toast.makeText(this, "Please enter a product name to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean result = databaseHelper.deleteProduct(productName);
        if (result) {
            Toast.makeText(this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
            clearProductDetails();
        } else {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
        }
    }
}