package com.ucucite.block_one_mob_dev;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 3; // Incremented version for schema changes

    // Single table for all user information
    private static final String TABLE_NAME = "users";

    // Basic signup columns
    private static final String COL_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";

    // Additional profile columns (nullable initially)
    private static final String COL_FIRST_NAME = "first_name";
    private static final String COL_LAST_NAME = "last_name";
    private static final String COL_PHONE_NUMBER = "phone_number";
    private static final String COL_PROVINCE = "province";
    private static final String COL_TOWN = "town";
    private static final String COL_BARANGAY = "barangay";
    private static final String COL_HOUSE_STREET = "house_street";
    private static final String COL_PROFILE_IMAGE_PATH = "profile_image_path";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT NOT NULL, " +
                COL_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_PASSWORD + " TEXT NOT NULL, " +
                COL_FIRST_NAME + " TEXT, " +
                COL_LAST_NAME + " TEXT, " +
                COL_PHONE_NUMBER + " TEXT, " +
                COL_PROVINCE + " TEXT, " +
                COL_TOWN + " TEXT, " +
                COL_BARANGAY + " TEXT, " +
                COL_HOUSE_STREET + " TEXT, " +
                COL_PROFILE_IMAGE_PATH + " TEXT" +
                ")";
        db.execSQL(createTable);
        Log.d("DatabaseHelper", "Table created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert basic user info during signup (additional fields will be null initially)
    public boolean insertUser(String username, String email, String password) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_USERNAME, username);
            values.put(COL_EMAIL, email);
            values.put(COL_PASSWORD, password);
            // Additional fields are left as null initially

            long result = db.insert(TABLE_NAME, null, values);
            Log.d("DatabaseHelper", "Insert user result: " + result);
            Log.d("DatabaseHelper", "Inserted user - Username: " + username + ", Email: " + email);

            return result != -1;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error inserting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    // Check if user exists by email
    public boolean checkUserExists(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_NAME,
                    new String[]{COL_ID},
                    COL_EMAIL + "=?",
                    new String[]{email},
                    null, null, null);

            boolean exists = cursor.moveToFirst();
            Log.d("DatabaseHelper", "User exists check for " + email + ": " + exists);
            return exists;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking user exists: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    // Get user by email and password (for login)
    public boolean validateUser(String email, String password) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_NAME,
                    new String[]{COL_ID},
                    COL_EMAIL + "=? AND " + COL_PASSWORD + "=?",
                    new String[]{email, password},
                    null, null, null);

            boolean isValid = cursor.moveToFirst();
            Log.d("DatabaseHelper", "User validation for " + email + ": " + isValid);
            return isValid;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error validating user: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    // Get username by email (used in Edit_Info activity)
    public String getUsernameByEmail(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_NAME,
                    new String[]{COL_USERNAME},
                    COL_EMAIL + "=?",
                    new String[]{email},
                    null, null, null);

            if (cursor.moveToFirst()) {
                String username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME));
                Log.d("DatabaseHelper", "Username found for " + email + ": " + username);
                return username;
            }
            Log.d("DatabaseHelper", "No username found for email: " + email);
            return null;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting username by email: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }
// Add this method to your DatabaseHelper class

    // Update user address information (for Edit_Address activity)
    public boolean updateUserAddress(String email, String province, String town, String barangay, String houseStreet) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_PROVINCE, province);
            values.put(COL_TOWN, town);
            values.put(COL_BARANGAY, barangay);
            values.put(COL_HOUSE_STREET, houseStreet);

            int rowsAffected = db.update(TABLE_NAME, values, COL_EMAIL + "=?", new String[]{email});
            Log.d("DatabaseHelper", "Update user address result: " + rowsAffected + " rows affected");
            Log.d("DatabaseHelper", "Updated address for email: " + email +
                    " - Province: " + province + ", Town: " + town +
                    ", Barangay: " + barangay + ", House/Street: " + houseStreet);

            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error updating user address: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
    // Get complete user information by email
    public UserInfo getUserByEmail(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_NAME,
                    null, // Select all columns
                    COL_EMAIL + "=?",
                    new String[]{email},
                    null, null, null);

            if (cursor.moveToFirst()) {
                UserInfo userInfo = new UserInfo();
                userInfo.id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                userInfo.username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME));
                userInfo.email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL));
                userInfo.password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD));
                userInfo.firstName = cursor.getString(cursor.getColumnIndexOrThrow(COL_FIRST_NAME));
                userInfo.lastName = cursor.getString(cursor.getColumnIndexOrThrow(COL_LAST_NAME));
                userInfo.phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE_NUMBER));
                userInfo.province = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROVINCE));
                userInfo.town = cursor.getString(cursor.getColumnIndexOrThrow(COL_TOWN));
                userInfo.barangay = cursor.getString(cursor.getColumnIndexOrThrow(COL_BARANGAY));
                userInfo.houseStreet = cursor.getString(cursor.getColumnIndexOrThrow(COL_HOUSE_STREET));
                userInfo.profileImagePath = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROFILE_IMAGE_PATH));

                Log.d("DatabaseHelper", "User info retrieved for: " + email);
                return userInfo;
            }
            Log.d("DatabaseHelper", "No user found for email: " + email);
            return null;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting user by email: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    // Update additional user information (for Edit_Info activity)
    public boolean updateUserInfo(String email, String firstName, String lastName, String phoneNumber,
                                  String province, String town, String barangay, String houseStreet) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_FIRST_NAME, firstName);
            values.put(COL_LAST_NAME, lastName);
            values.put(COL_PHONE_NUMBER, phoneNumber);
            values.put(COL_PROVINCE, province);
            values.put(COL_TOWN, town);
            values.put(COL_BARANGAY, barangay);
            values.put(COL_HOUSE_STREET, houseStreet);

            int rowsAffected = db.update(TABLE_NAME, values, COL_EMAIL + "=?", new String[]{email});
            Log.d("DatabaseHelper", "Update user info result: " + rowsAffected + " rows affected");
            Log.d("DatabaseHelper", "Updated user info for email: " + email);

            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error updating user info: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    // Update profile image path
    public boolean updateProfileImage(String email, String imagePath) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_PROFILE_IMAGE_PATH, imagePath);

            int rowsAffected = db.update(TABLE_NAME, values, COL_EMAIL + "=?", new String[]{email});
            Log.d("DatabaseHelper", "Update profile image result: " + rowsAffected + " rows affected");

            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error updating profile image: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    // Update username
    public boolean updateUsername(String email, String newUsername) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_USERNAME, newUsername);

            int rowsAffected = db.update(TABLE_NAME, values, COL_EMAIL + "=?", new String[]{email});
            Log.d("DatabaseHelper", "Update username result: " + rowsAffected + " rows affected");

            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error updating username: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    // Update password
    public boolean updatePassword(String email, String newPassword) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_PASSWORD, newPassword);

            int rowsAffected = db.update(TABLE_NAME, values, COL_EMAIL + "=?", new String[]{email});
            Log.d("DatabaseHelper", "Update password result: " + rowsAffected + " rows affected");

            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error updating password: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    // Delete user by email
    public boolean deleteUser(String email) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            int rowsDeleted = db.delete(TABLE_NAME, COL_EMAIL + "=?", new String[]{email});
            Log.d("DatabaseHelper", "Delete user result: " + rowsDeleted + " rows deleted");

            return rowsDeleted > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error deleting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    // Check if profile is complete (has all required additional info)
    public boolean isProfileComplete(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_NAME,
                    new String[]{COL_FIRST_NAME, COL_LAST_NAME, COL_PHONE_NUMBER,
                            COL_PROVINCE, COL_TOWN, COL_BARANGAY, COL_HOUSE_STREET},
                    COL_EMAIL + "=?",
                    new String[]{email},
                    null, null, null);

            if (cursor.moveToFirst()) {
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow(COL_FIRST_NAME));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COL_LAST_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE_NUMBER));
                String province = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROVINCE));
                String town = cursor.getString(cursor.getColumnIndexOrThrow(COL_TOWN));
                String barangay = cursor.getString(cursor.getColumnIndexOrThrow(COL_BARANGAY));
                String houseStreet = cursor.getString(cursor.getColumnIndexOrThrow(COL_HOUSE_STREET));

                boolean isComplete = firstName != null && !firstName.trim().isEmpty() &&
                        lastName != null && !lastName.trim().isEmpty() &&
                        phoneNumber != null && !phoneNumber.trim().isEmpty() &&
                        province != null && !province.trim().isEmpty() &&
                        town != null && !town.trim().isEmpty() &&
                        barangay != null && !barangay.trim().isEmpty() &&
                        houseStreet != null && !houseStreet.trim().isEmpty();

                Log.d("DatabaseHelper", "Profile complete check for " + email + ": " + isComplete);
                return isComplete;
            }
            return false;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking profile completeness: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    // Log all users for debugging (used in Edit_Info activity)
    public void logAllUsers() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

            Log.d("DatabaseHelper", "=== ALL USERS IN DATABASE ===");
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                    String username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME));
                    String email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL));
                    String firstName = cursor.getString(cursor.getColumnIndexOrThrow(COL_FIRST_NAME));
                    String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COL_LAST_NAME));
                    String phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE_NUMBER));
                    String province = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROVINCE));
                    String town = cursor.getString(cursor.getColumnIndexOrThrow(COL_TOWN));
                    String barangay = cursor.getString(cursor.getColumnIndexOrThrow(COL_BARANGAY));
                    String house = cursor.getString(cursor.getColumnIndexOrThrow(COL_HOUSE_STREET));

                    Log.d("DatabaseHelper", "User " + id + ": " + username + " (" + email + ")");
                    Log.d("DatabaseHelper", "  Name: " + firstName + " " + lastName);
                    Log.d("DatabaseHelper", "  Phone: " + phone);
                    Log.d("DatabaseHelper", "  Address: " + house + ", " + barangay + ", " + town + ", " + province);
                    Log.d("DatabaseHelper", "  ---");
                } while (cursor.moveToNext());
            } else {
                Log.d("DatabaseHelper", "No users found in database");
            }
            Log.d("DatabaseHelper", "=== END USER LIST ===");
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error logging all users: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    // Log specific user by email
    public void logUserByEmail(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_NAME, null, COL_EMAIL + "=?", new String[]{email}, null, null, null);

            Log.d("DatabaseHelper", "=== USER INFO FOR: " + email + " ===");
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME));
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow(COL_FIRST_NAME));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COL_LAST_NAME));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE_NUMBER));
                String province = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROVINCE));
                String town = cursor.getString(cursor.getColumnIndexOrThrow(COL_TOWN));
                String barangay = cursor.getString(cursor.getColumnIndexOrThrow(COL_BARANGAY));
                String house = cursor.getString(cursor.getColumnIndexOrThrow(COL_HOUSE_STREET));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROFILE_IMAGE_PATH));

                Log.d("DatabaseHelper", "ID: " + id);
                Log.d("DatabaseHelper", "Username: " + username);
                Log.d("DatabaseHelper", "Email: " + email);
                Log.d("DatabaseHelper", "First Name: " + firstName);
                Log.d("DatabaseHelper", "Last Name: " + lastName);
                Log.d("DatabaseHelper", "Phone: " + phone);
                Log.d("DatabaseHelper", "Province: " + province);
                Log.d("DatabaseHelper", "Town: " + town);
                Log.d("DatabaseHelper", "Barangay: " + barangay);
                Log.d("DatabaseHelper", "House/Street: " + house);
                Log.d("DatabaseHelper", "Profile Image: " + imagePath);
            } else {
                Log.d("DatabaseHelper", "No user found with email: " + email);
            }
            Log.d("DatabaseHelper", "=== END USER INFO ===");
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error logging user by email: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    // Log database statistics
    public void logDatabaseStats() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

            if (cursor.moveToFirst()) {
                int totalUsers = cursor.getInt(0);
                Log.d("DatabaseHelper", "=== DATABASE STATISTICS ===");
                Log.d("DatabaseHelper", "Total Users: " + totalUsers);
                Log.d("DatabaseHelper", "Database Name: " + DATABASE_NAME);
                Log.d("DatabaseHelper", "Database Version: " + DATABASE_VERSION);
                Log.d("DatabaseHelper", "Table Name: " + TABLE_NAME);
                Log.d("DatabaseHelper", "=== END STATISTICS ===");
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error logging database stats: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    // Log table structure
    public void logTableStructure() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("PRAGMA table_info(" + TABLE_NAME + ")", null);

            Log.d("DatabaseHelper", "=== TABLE STRUCTURE ===");
            if (cursor.moveToFirst()) {
                do {
                    int cid = cursor.getInt(0);
                    String name = cursor.getString(1);
                    String type = cursor.getString(2);
                    int notNull = cursor.getInt(3);
                    String defaultValue = cursor.getString(4);
                    int pk = cursor.getInt(5);

                    Log.d("DatabaseHelper", "Column " + cid + ": " + name + " (" + type + ")" +
                            (pk == 1 ? " PRIMARY KEY" : "") +
                            (notNull == 1 ? " NOT NULL" : "") +
                            (defaultValue != null ? " DEFAULT " + defaultValue : ""));
                } while (cursor.moveToNext());
            }
            Log.d("DatabaseHelper", "=== END TABLE STRUCTURE ===");
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error logging table structure: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    // User info data class
    public static class UserInfo {
        public int id;
        public String username;
        public String email;
        public String password; // Note: In production, passwords should be hashed
        public String firstName;
        public String lastName;
        public String phoneNumber;
        public String province;
        public String town;
        public String barangay;
        public String houseStreet;
        public String profileImagePath;

        public String getFullName() {
            if (firstName != null && lastName != null) {
                return firstName + " " + lastName;
            } else if (firstName != null) {
                return firstName;
            } else if (lastName != null) {
                return lastName;
            }
            return "";
        }

        public String getFullAddress() {
            StringBuilder address = new StringBuilder();
            if (houseStreet != null && !houseStreet.trim().isEmpty()) {
                address.append(houseStreet);
            }
            if (barangay != null && !barangay.trim().isEmpty()) {
                if (address.length() > 0) address.append(", ");
                address.append(barangay);
            }
            if (town != null && !town.trim().isEmpty()) {
                if (address.length() > 0) address.append(", ");
                address.append(town);
            }
            if (province != null && !province.trim().isEmpty()) {
                if (address.length() > 0) address.append(", ");
                address.append(province);
            }
            return address.toString();
        }
    }
}