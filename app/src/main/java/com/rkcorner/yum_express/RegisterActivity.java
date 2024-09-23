package com.rkcorner.yum_express;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText etUsername = findViewById(R.id.et_username);
        EditText etEmail = findViewById(R.id.et_register_Email);
        EditText etPassword = findViewById(R.id.et_register_password);
        EditText etConfirmPassword = findViewById(R.id.et_register_Confirm_Password);
        EditText etMobile = findViewById(R.id.et_register_Contact_Number);
        Button btnRegister = findViewById(R.id.btn_register);
        Button btnLogin = findViewById(R.id.btn_login);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();
            String mobile = etMobile.getText().toString();

            if (password.equals(confirmPassword) && !password.isEmpty() && !username.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "well done! Let me insert your info in DB!", Toast.LENGTH_SHORT).show();
                // Connection with DB
                DatabaseHelper dbHelper = new DatabaseHelper(RegisterActivity.this);
                boolean isInserted = dbHelper.insertUser(username, email, password, mobile);

                if (isInserted) {
                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RegisterActivity.this, "Passwords do not match or empty password or empty username!", Toast.LENGTH_SHORT).show();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}