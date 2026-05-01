package com.example.myproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etEmail, etMobile;
    private ImageView ivPhoto;
    private Button btnCapture, btnRegister, btnLogin;
    private Bitmap capturedBitmap;
    private DBHelper dbHelper;
    private String savedImagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        ivPhoto = findViewById(R.id.ivPhoto);
        btnCapture = findViewById(R.id.btnCapture);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);

        ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            capturedBitmap = (Bitmap) extras.get("data");
                            ivPhoto.setImageBitmap(capturedBitmap);
                            savedImagePath = saveToInternalStorage(capturedBitmap);
                        }
                    }
                }
        );

        ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraLauncher.launch(intent);
                    } else {
                        Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        btnCapture.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraLauncher.launch(intent);
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        btnRegister.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            String mail = etEmail.getText().toString().trim();
            String mob = etMobile.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty() || mail.isEmpty() || mob.isEmpty() || capturedBitmap == null) {
                Toast.makeText(this, "All fields are required and image must be captured", Toast.LENGTH_SHORT).show();
            } else {
                byte[] imgData = getBytesFromBitmap(capturedBitmap);
                boolean success = dbHelper.insertUser(user, pass, mail, mob, imgData, savedImagePath);
                if (success) {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                    // Optional: clear fields or navigate to login
                } else {
                    Toast.makeText(this, "Registration failed. Username might already exist.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        File directory = getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory, "profile_" + System.currentTimeMillis() + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }
}