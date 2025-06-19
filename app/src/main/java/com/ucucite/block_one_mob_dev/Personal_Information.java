package com.ucucite.block_one_mob_dev;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Personal_Information extends AppCompatActivity {

    private static final String TAG = "PersonalInformation";

    private EditText etFirstName, etLastName, etPhoneNumber, etUsername, etCurrentPassword, etNewPassword, etConfirmPassword;
    private Button btnSaveChanges, btnChangePassword;
    private DatabaseHelper dbHelper;
    private String userEmail;
    private DatabaseHelper.UserInfo currentUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Get email from intent
        userEmail = getIntent().getStringExtra("email");
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Error: No user email provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup toolbar
        setupToolbar();

        // Initialize views
        initializeViews();

        // Load current user data
        loadUserData();

        // Setup click listeners
        setupClickListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Personal Information");
        }
    }

    private void initializeViews() {
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etUsername = findViewById(R.id.et_username);
        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        btnSaveChanges = findViewById(R.id.btn_save_changes);
        btnChangePassword = findViewById(R.id.btn_change_password);
    }

    private void loadUserData() {
        currentUserInfo = dbHelper.getUserByEmail(userEmail);
        if (currentUserInfo != null) {
            // Populate fields with existing data
            etFirstName.setText(currentUserInfo.firstName != null ? currentUserInfo.firstName : "");
            etLastName.setText(currentUserInfo.lastName != null ? currentUserInfo.lastName : "");
            etPhoneNumber.setText(currentUserInfo.phoneNumber != null ? currentUserInfo.phoneNumber : "");
            etUsername.setText(currentUserInfo.username != null ? currentUserInfo.username : "");

            Log.d(TAG, "User data loaded successfully for: " + userEmail);
        } else {
            Toast.makeText(this, "Error loading user data", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Failed to load user data for email: " + userEmail);
        }
    }

    private void setupClickListeners() {
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePersonalInfo();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    private void savePersonalInfo() {
        // Get input values
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String username = etUsername.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(firstName)) {
            etFirstName.setError("First name is required");
            etFirstName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            etLastName.setError("Last name is required");
            etLastName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            etPhoneNumber.setError("Phone number is required");
            etPhoneNumber.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        }

        // Validate phone number format (Philippine format)
        if (!isValidPhoneNumber(phoneNumber)) {
            etPhoneNumber.setError("Please enter a valid Philippine phone number");
            etPhoneNumber.requestFocus();
            return;
        }

        // Check if username is already taken (if changed)
        if (!username.equals(currentUserInfo.username)) {
            if (isUsernameTaken(username)) {
                etUsername.setError("Username is already taken");
                etUsername.requestFocus();
                return;
            }
        }

        // Update user information
        boolean success = updateUserPersonalInfo(firstName, lastName, phoneNumber, username);

        if (success) {
            Toast.makeText(this, "Personal information updated successfully", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Personal info updated successfully for: " + userEmail);
            // Reload user data to reflect changes
            loadUserData();
        } else {
            Toast.makeText(this, "Failed to update personal information", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Failed to update personal info for: " + userEmail);
        }
    }

    private void changePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(currentPassword)) {
            etCurrentPassword.setError("Current password is required");
            etCurrentPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("New password is required");
            etNewPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Please confirm your new password");
            etConfirmPassword.requestFocus();
            return;
        }

        // Validate current password
        if (!dbHelper.validateUser(userEmail, currentPassword)) {
            etCurrentPassword.setError("Current password is incorrect");
            etCurrentPassword.requestFocus();
            return;
        }

        // Validate new password strength
        if (newPassword.length() < 6) {
            etNewPassword.setError("Password must be at least 6 characters long");
            etNewPassword.requestFocus();
            return;
        }

        // Check if new passwords match
        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        // Check if new password is different from current
        if (newPassword.equals(currentPassword)) {
            etNewPassword.setError("New password must be different from current password");
            etNewPassword.requestFocus();
            return;
        }

        // Update password
        boolean success = dbHelper.updatePassword(userEmail, newPassword);

        if (success) {
            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Password updated successfully for: " + userEmail);

            // Clear password fields
            etCurrentPassword.setText("");
            etNewPassword.setText("");
            etConfirmPassword.setText("");
        } else {
            Toast.makeText(this, "Failed to change password", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Failed to update password for: " + userEmail);
        }
    }

    private boolean updateUserPersonalInfo(String firstName, String lastName, String phoneNumber, String username) {
        try {
            // Keep existing address information
            String province = currentUserInfo.province;
            String town = currentUserInfo.town;
            String barangay = currentUserInfo.barangay;
            String houseStreet = currentUserInfo.houseStreet;

            // Update user info including personal details
            boolean infoUpdated = dbHelper.updateUserInfo(userEmail, firstName, lastName, phoneNumber,
                    province, town, barangay, houseStreet);

            // Update username separately if it changed
            boolean usernameUpdated = true;
            if (!username.equals(currentUserInfo.username)) {
                usernameUpdated = dbHelper.updateUsername(userEmail, username);
            }

            return infoUpdated && usernameUpdated;
        } catch (Exception e) {
            Log.e(TAG, "Error updating user personal info: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Remove all non-digit characters
        String digitsOnly = phoneNumber.replaceAll("[^0-9]", "");

        // Philippine phone number validation
        // Mobile numbers: 09xxxxxxxxx (11 digits) or +639xxxxxxxxx (13 digits with country code)
        // Landline: Various formats but typically 7-8 digits with area codes

        if (digitsOnly.length() == 11 && digitsOnly.startsWith("09")) {
            return true; // Mobile format: 09xxxxxxxxx
        } else if (digitsOnly.length() == 13 && digitsOnly.startsWith("639")) {
            return true; // International mobile format: +639xxxxxxxxx (without +)
        } else if (digitsOnly.length() >= 7 && digitsOnly.length() <= 11) {
            return true; // Landline or other valid formats
        }

        return false;
    }

    private boolean isUsernameTaken(String username) {
        // Get all users and check if username exists (excluding current user)
        DatabaseHelper.UserInfo existingUser = null;

        // Since we don't have a direct method to get user by username,
        // we'll check by attempting to update and seeing if there's a conflict
        // For now, we'll assume username is available
        // In a real app, you'd want to add a method to check username uniqueness

        return false; // Simplified for this implementation
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}