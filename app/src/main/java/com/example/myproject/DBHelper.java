package com.example.myproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "StudentAppDB.db";
    public static final int DBVERSION = 5;

    public static final String TABLE_USERS = "users";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";
    public static final String COL_EMAIL = "email";
    public static final String COL_MOBILE = "mobile";
    public static final String COL_IMAGE = "image";
    public static final String COL_IMAGE_PATH = "imagePath";

    public DBHelper(Context context) {
        super(context, DBNAME, null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL("CREATE TABLE " + TABLE_USERS + "(" +
                COL_USERNAME + " TEXT PRIMARY KEY, " +
                COL_PASSWORD + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_MOBILE + " TEXT, " +
                COL_IMAGE + " BLOB, " +
                COL_IMAGE_PATH + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int oldVersion, int newVersion) {
        MyDB.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(MyDB);
    }

    public boolean insertUser(String username, String password, String email, String mobile, byte[] image, String imagePath) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USERNAME, username);
        contentValues.put(COL_PASSWORD, password);
        contentValues.put(COL_EMAIL, email);
        contentValues.put(COL_MOBILE, mobile);
        contentValues.put(COL_IMAGE, image);
        contentValues.put(COL_IMAGE_PATH, imagePath);
        long result = MyDB.insert(TABLE_USERS, null, contentValues);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase MyDB = this.getReadableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?", new String[]{username, password});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public Cursor getUser(String username) {
        SQLiteDatabase MyDB = this.getReadableDatabase();
        return MyDB.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + " = ?", new String[]{username});
    }
}