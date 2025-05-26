package com.ucucite.block_one_mob_dev;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_NAME = "users";
    private static final String COL_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT, " +
                COL_EMAIL + " TEXT UNIQUE, " +
                COL_PASSWORD + " TEXT, " +
                "first_name TEXT, " +
                "last_name TEXT, " +
                "phone_number TEXT, " +
                "province TEXT, " +
                "town TEXT, " +
                "barangay TEXT, " +
                "house_street TEXT" +
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public boolean checkUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COL_ID},
                COL_EMAIL + "=?",
                new String[]{email},
                null, null, null);

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean updateUserInfo(String email, String firstName, String lastName, String phoneNumber,
                                  String province, String town, String barangay, String houseStreet) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("first_name", firstName);
        values.put("last_name", lastName);
        values.put("phone_number", phoneNumber);
        values.put("province", province);
        values.put("town", town);
        values.put("barangay", barangay);
        values.put("house_street", houseStreet);

        int rowsAffected = db.update(TABLE_NAME, values, COL_EMAIL + "=?", new String[]{email});
        return rowsAffected > 0;
    }

    public void logAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD));
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone_number"));
                String province = cursor.getString(cursor.getColumnIndexOrThrow("province"));
                String town = cursor.getString(cursor.getColumnIndexOrThrow("town"));
                String barangay = cursor.getString(cursor.getColumnIndexOrThrow("barangay"));
                String houseStreet = cursor.getString(cursor.getColumnIndexOrThrow("house_street"));

                Log.d("DB_LOG", "User: " + username +
                        ", Email: " + email +
                        ", Password: " + password +
                        ", First Name: " + firstName +
                        ", Last Name: " + lastName +
                        ", Phone: " + phone +
                        ", Province: " + province +
                        ", Town: " + town +
                        ", Barangay: " + barangay +
                        ", House/Street: " + houseStreet);
            } while (cursor.moveToNext());
        } else {
            Log.d("DB_LOG", "No users found in database.");
        }

        cursor.close();
    }
}
