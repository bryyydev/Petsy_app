package com.ucucite.block_one_mob_dev;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Log_in extends AppCompatActivity {

    private static final String TAG = "Log_in";

    private EditText etEmail, etPassword;
    private Button btnLogin, btnSignUp;
    private TextView tvForgotPassword;
    private ImageView ivTogglePassword;
    private LinearLayout btnGoogle, btnFacebook;
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
        btnGoogle = findViewById(R.id.btn_log_in_google); // Changed from Button to LinearLayout
        btnFacebook = findViewById(R.id.btn_log_fb);       // Changed from Button to LinearLayout
        tvForgotPassword = findViewById(R.id.tv_frgt_password);
        btnSignUp = findViewById(R.id.btn_tosign_up);
        ivTogglePassword = findViewById(R.id.iv_toggle_password);

        // Login Button Click Event
        btnLogin.setOnClickListener(v -> performLogin());

        // Google Login Click
        btnGoogle.setOnClickListener(v ->
                Toast.makeText(Log_in.this, "Google Login Clicked!", Toast.LENGTH_SHORT).show()
        );

        // Facebook Login Click
        btnFacebook.setOnClickListener(v ->
                Toast.makeText(Log_in.this, "Facebook Login Clicked!", Toast.LENGTH_SHORT).show()
        );

        // Forgot Password Click
        tvForgotPassword.setOnClickListener(v -> handleForgotPassword());

        // Sign Up Click
        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, Sign_up.class);
            startActivity(intent);
        });

        // Toggle Password Visibility
        ivTogglePassword.setOnClickListener(v -> togglePasswordVisibility());
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInput(email, password)) return;

        btnLogin.setEnabled(false);

        try {
            if (!databaseHelper.checkUserExists(email)) {
                Toast.makeText(this, "No account found with this email address", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Login attempt with non-existent email: " + email);
                btnLogin.setEnabled(true);
                return;
            }

            if (databaseHelper.validateUser(email, password)) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Successful login for user: " + email);
                navigateToMainActivity(email);
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Invalid login attempt for email: " + email);
                etPassword.setText("");
                btnLogin.setEnabled(true);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error during login process: " + e.getMessage(), e);
            Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_LONG).show();
            btnLogin.setEnabled(true);
        }
    }

    private boolean validateInput(String email, String password) {
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

        if (!isValidEmail(email)) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void navigateToMainActivity(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_email", email);
        editor.apply();

        Log.d(TAG, "User email saved to SharedPreferences: " + email);

        Intent intent = new Intent(Log_in.this, nav_bar.class);
        intent.putExtra("EMAIL", email);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

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

        if (databaseHelper.checkUserExists(email)) {
            Toast.makeText(this, "Password reset instructions would be sent to: " + email,
                    Toast.LENGTH_LONG).show();
            Log.d(TAG, "Password reset requested for: " + email);
        } else {
            Toast.makeText(this, "No account found with this email address",
                    Toast.LENGTH_LONG).show();
            Log.d(TAG, "Password reset attempted for non-existent email: " + email);
        }
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivTogglePassword.setImageResource(R.drawable.ic_eye_closed);
            isPasswordVisible = false;
        } else {
            etPassword.setTransformationMethod(null);
            ivTogglePassword.setImageResource(R.drawable.ic_eye_open);
            isPasswordVisible = true;
        }
        etPassword.setSelection(etPassword.getText().length());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            Log.d(TAG, "Activity destroyed");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (btnLogin != null) {
            btnLogin.setEnabled(true);
        }
    }
}
