package com.rkcorner.yum_express;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductAdapter extends CursorAdapter {

    private DatabaseHelper databaseHelper;

    public ProductAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        databaseHelper = new DatabaseHelper(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.list_item_product, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.text_view_product_name);
        TextView priceTextView = view.findViewById(R.id.text_view_product_price);
        TextView quantityTextView = view.findViewById(R.id.text_view_product_quantity);
        ImageView productImageView = view.findViewById(R.id.image_view_product);
        Button buttonCart = view.findViewById(R.id.button_cart);
        Button buttonOrder = view.findViewById(R.id.button_order);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_NAME));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_QUANTITY));
        byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_IMAGE_URI));

        nameTextView.setText(name);
        priceTextView.setText(String.valueOf(price));
        quantityTextView.setText(String.valueOf(quantity));
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        productImageView.setImageBitmap(bitmap);

        buttonCart.setOnClickListener(v -> {
            Log.d("ProductAdapter", "Cart button clicked for product: " + name);
            addToCart(context, name, price, imageBytes, quantity);
        });

        buttonOrder.setOnClickListener(v -> {
            Log.d("ProductAdapter", "Order button clicked for product: " + name);
            placeOrder(context, name, price, imageBytes, quantity);
        });
    }

    private void addToCart(Context context, String productName, double productPrice, byte[] productImageUri, int quantity) {
        try {
            databaseHelper.addToCart(productName, productPrice, productImageUri, quantity);
            Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("ProductAdapter", "Error adding to cart: " + e.getMessage());
            Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show();
        }
    }

    private void placeOrder(Context context, String productName, double productPrice, byte[] productImageUri, int quantity) {
        try {
            databaseHelper.placeOrder(productName, productPrice, productImageUri, quantity);
            Toast.makeText(context, "Order placed successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("ProductAdapter", "Error placing order: " + e.getMessage());
            Toast.makeText(context, "Failed to place order", Toast.LENGTH_SHORT).show();
        }
    }
}