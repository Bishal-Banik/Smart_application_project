package com.rkcorner.yum_express;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class ProductsDisplay extends AppCompatActivity {

    private ListView listViewProducts;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_display);

        listViewProducts = findViewById(R.id.list_view_products);
        databaseHelper = new DatabaseHelper(this);

        displayProducts();
    }

    private void displayProducts() {
        Cursor cursor = databaseHelper.getAllProducts();
        ProductAdapter adapter = new ProductAdapter(this, cursor, 0);
        listViewProducts.setAdapter(adapter);
    }
}