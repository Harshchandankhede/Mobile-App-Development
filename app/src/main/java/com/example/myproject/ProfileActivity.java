package com.example.myproject;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView username, email, mobile;
    private ImageView image;
    private Button logout;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = findViewById(R.id.tvProfileUsername);
        email = findViewById(R.id.tvProfileEmail);
        mobile = findViewById(R.id.tvProfileMobile);
        image = findViewById(R.id.ivProfilePhoto);
        logout = findViewById(R.id.btnLogout);
        dbHelper = new DBHelper(this);

        String user = getIntent().getStringExtra("username");

        if (user != null) {
            loadUserData(user);
        } else {
            Toast.makeText(this, "No user specified", Toast.LENGTH_SHORT).show();
        }

        logout.setOnClickListener(view -> finish());
    }

    private void loadUserData(String user) {
        Cursor cursor = dbHelper.getUser(user);
        if (cursor != null && cursor.moveToFirst()) {
            int userIndex = cursor.getColumnIndex(DBHelper.COL_USERNAME);
            int emailIndex = cursor.getColumnIndex(DBHelper.COL_EMAIL);
            int mobileIndex = cursor.getColumnIndex(DBHelper.COL_MOBILE);
            int imageIndex = cursor.getColumnIndex(DBHelper.COL_IMAGE);

            if (userIndex != -1) username.setText("Username: " + cursor.getString(userIndex));
            if (emailIndex != -1) email.setText("Email: " + cursor.getString(emailIndex));
            if (mobileIndex != -1) mobile.setText("Mobile: " + cursor.getString(mobileIndex));
            
            if (imageIndex != -1) {
                byte[] img = cursor.getBlob(imageIndex);
                if (img != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                    image.setImageBitmap(bitmap);
                }
            }
            cursor.close();
        } else {
            Toast.makeText(this, "User details not found", Toast.LENGTH_SHORT).show();
        }
    }
}