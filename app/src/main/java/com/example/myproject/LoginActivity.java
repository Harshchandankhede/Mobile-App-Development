package com.example.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etLoginUsername);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLoginSubmit);
        db = new DBHelper(this);

        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            } else {
                if (db.checkUser(user, pass)) {
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                    intent.putExtra("username", user);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Login", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}