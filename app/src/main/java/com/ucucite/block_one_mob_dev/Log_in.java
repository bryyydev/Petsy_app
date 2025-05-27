package com.ucucite.block_one_mob_dev;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class Log_in extends AppCompatActivity {

    private static final String TAG = "Log_in";

    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoogle, btnFacebook, btnSignUp;
    private TextView tvForgotPassword;
    private ImageView ivTogglePassword;
    private boolean isPasswordVisible = false;

    // Database helper instance
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Initialize Views
        etEmail = findViewById(R.id.editText_gmail);
        etPassword = findViewById(R.id.editText_password);
        btnLogin = findViewById(R.id.btn_log_in);
        btnGoogle = findViewById(R.id.btn_log_in_google);
        btnFacebook = findViewById(R.id.btn_log_fb);
        tvForgotPassword = findViewById(R.id.tv_frgt_password);
        btnSignUp = findViewById(R.id.btn_tosign_up);
        ivTogglePassword = findViewById(R.id.iv_toggle_password);

        // Login Button Click Event
        btnLogin.setOnClickListener(v -> {
            performLogin();
        });

        // Google Login Click
        btnGoogle.setOnClickListener(v -> {
            Toast.makeText(Log_in.this, "Google Login Clicked!", Toast.LENGTH_SHORT).show();
        });

        // Facebook Login Click
        btnFacebook.setOnClickListener(v -> {
            Toast.makeText(Log_in.this, "Facebook Login Clicked!", Toast.LENGTH_SHORT).show();
        });

        // Forgot Password Click
        tvForgotPassword.setOnClickListener(v -> {
            handleForgotPassword();
        });

        // Sign Up Click
        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, Sign_up.class);
            startActivity(intent);
        });

        // Toggle Password Visibility
        ivTogglePassword.setOnClickListener(v -> {
            togglePasswordVisibility();
        });
    }

    /**
     * Performs login validation and authentication
     */
    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Input validation
        if (!validateInput(email, password)) {
            return;
        }

        // Disable login button to prevent multiple clicks
        btnLogin.setEnabled(false);

        try {
            // Check if user exists in database
            if (!databaseHelper.checkUserExists(email)) {
                Toast.makeText(this, "No account found with this email address", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Login attempt with non-existent email: " + email);
                btnLogin.setEnabled(true);
                return;
            }

            // Validate user credentials
            if (databaseHelper.validateUser(email, password)) {
                // Login successful
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Successful login for user: " + email);

                // Navigate to main activity
                navigateToMainActivity(email);

            } else {
                // Invalid credentials
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Invalid login attempt for email: " + email);

                // Clear password field for security
                etPassword.setText("");
                btnLogin.setEnabled(true);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error during login process: " + e.getMessage(), e);
            Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_LONG).show();
            btnLogin.setEnabled(true);
        }
    }

    /**
     * Validates user input
     */
    private boolean validateInput(String email, String password) {
        // Check if fields are empty
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        // Basic email format validation
        if (!isValidEmail(email)) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return false;
        }

        // Password length validation
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Basic email validation
     */
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Navigates to the main activity after successful login
     */
    private void navigateToMainActivity(String email) {
        // Save email to SharedPreferences for session management
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_email", email);
        editor.apply();

        Log.d(TAG, "User email saved to SharedPreferences: " + email);

        Intent intent = new Intent(Log_in.this, nav_bar.class);
        intent.putExtra("EMAIL", email);

        // Add flags to clear the activity stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }

    /**
     * Handles forgot password functionality
     */
    private void handleForgotPassword() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email address first", Toast.LENGTH_SHORT).show();
            etEmail.requestFocus();
            return;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            etEmail.requestFocus();
            return;
        }

        // Check if user exists
        if (databaseHelper.checkUserExists(email)) {
            Toast.makeText(this, "Password reset instructions would be sent to: " + email,
                    Toast.LENGTH_LONG).show();
            Log.d(TAG, "Password reset requested for: " + email);

            // TODO: Implement actual password reset functionality
            // This could involve sending an email, generating a reset token, etc.

        } else {
            Toast.makeText(this, "No account found with this email address",
                    Toast.LENGTH_LONG).show();
            Log.d(TAG, "Password reset attempted for non-existent email: " + email);
        }
    }

    /**
     * Toggles password visibility
     */
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivTogglePassword.setImageResource(R.drawable.ic_eye_closed);
            isPasswordVisible = false;
        } else {
            // Show password
            etPassword.setTransformationMethod(null);
            ivTogglePassword.setImageResource(R.drawable.ic_eye_open);
            isPasswordVisible = true;
        }

        // Move cursor to end of text
        etPassword.setSelection(etPassword.getText().length());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close database connection if needed
        if (databaseHelper != null) {
            // DatabaseHelper handles closing connections in each method
            Log.d(TAG, "Activity destroyed");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Re-enable login button when returning to this activity
        if (btnLogin != null) {
            btnLogin.setEnabled(true);
        }
    }
}