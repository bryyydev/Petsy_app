package com.ucucite.block_one_mob_dev;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Shipping_Address extends AppCompatActivity {

    private ImageView editIcon, backArrow, deleteIcon;
    private TextView fullNameLabel, phoneNumber, address;
    private DatabaseHelper databaseHelper;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_address);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Initialize views
        initializeViews();

        // Get current user email from SharedPreferences or Intent
        getCurrentUserEmail();

        // Load and display user information
        loadUserInformation();

        // Set up click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        editIcon = findViewById(R.id.edit_icon);
        backArrow = findViewById(R.id.imageView3);
        deleteIcon = findViewById(R.id.delete_ic);
        fullNameLabel = findViewById(R.id.fullNameLabel);
        phoneNumber = findViewById(R.id.phoneNumber);
        address = findViewById(R.id.address);
    }

    private void getCurrentUserEmail() {
        // First, try to get email from SharedPreferences (most common approach)
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentUserEmail = sharedPreferences.getString("user_email", null);

        // If not found in SharedPreferences, try to get from Intent with multiple possible keys
        if (currentUserEmail == null) {
            Intent intent = getIntent();
            currentUserEmail = intent.getStringExtra("user_email");

            // Also try the EMAIL key used in login activity
            if (currentUserEmail == null) {
                currentUserEmail = intent.getStringExtra("EMAIL");
            }
        }

        Log.d("Shipping_Address", "Current user email: " + currentUserEmail);

        if (currentUserEmail == null) {
            Toast.makeText(this, "User session not found. Please login again.", Toast.LENGTH_SHORT).show();
            // Redirect to login activity
            Intent loginIntent = new Intent(this, Log_in.class);
            startActivity(loginIntent);
            finish();
        }
    }

    private void loadUserInformation() {
        if (currentUserEmail == null) {
            return;
        }

        // Get user information from database
        DatabaseHelper.UserInfo userInfo = databaseHelper.getUserByEmail(currentUserEmail);

        if (userInfo != null) {
            // Display full name
            String fullName = userInfo.getFullName();
            if (fullName != null && !fullName.trim().isEmpty()) {
                fullNameLabel.setText(fullName);
            } else {
                fullNameLabel.setText(userInfo.username); // Fallback to username
            }

            // Display phone number
            if (userInfo.phoneNumber != null && !userInfo.phoneNumber.trim().isEmpty()) {
                phoneNumber.setText(userInfo.phoneNumber);
            } else {
                phoneNumber.setText("No phone number");
            }

            // Display address
            String fullAddress = userInfo.getFullAddress();
            if (fullAddress != null && !fullAddress.trim().isEmpty()) {
                address.setText(fullAddress);
            } else {
                address.setText("No address information");
            }

            Log.d("Shipping_Address", "User information loaded successfully");
            Log.d("Shipping_Address", "Full Name: " + fullName);
            Log.d("Shipping_Address", "Phone: " + userInfo.phoneNumber);
            Log.d("Shipping_Address", "Address: " + fullAddress);

        } else {
            Toast.makeText(this, "Failed to load user information", Toast.LENGTH_SHORT).show();
            Log.e("Shipping_Address", "Failed to get user info for email: " + currentUserEmail);

            // Set default values
            fullNameLabel.setText("User not found");
            phoneNumber.setText("No phone number");
            address.setText("No address information");
        }
    }

    private void setupClickListeners() {
        // Edit icon click listener
        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Edit_Address activity and pass user email
                Intent intent = new Intent(Shipping_Address.this, Edit_Address.class);
                intent.putExtra("user_email", currentUserEmail);
                startActivity(intent);
            }
        });

        // Back arrow click listener
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to previous activity
            }
        });

        // Delete icon click listener (optional - you can implement address deletion)
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // You can implement address deletion functionality here
                Toast.makeText(Shipping_Address.this, "Delete functionality not implemented yet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload user information when returning from edit activity
        loadUserInformation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close database helper
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}