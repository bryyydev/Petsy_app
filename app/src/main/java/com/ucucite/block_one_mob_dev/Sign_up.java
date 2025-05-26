package com.ucucite.block_one_mob_dev;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

import com.ucucite.block_one_mob_dev.DatabaseHelper;
import androidx.appcompat.app.AppCompatActivity;

public class Sign_up extends AppCompatActivity {

    private EditText editTextUsername, editTextEmail, editTextPassword, editTextConfirmPassword;
    private ImageView togglePassword, toggleConfirmPassword;
    private CheckBox checkboxTerms;
    private Button btnSignUp, btnSignUpGoogle, btnSignUpFacebook;
    private TextView tvLoginRedirect;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initializeViews();
        setupPasswordToggles();
        setupSignUpLogic();
        setupLoginRedirect();
    }

    private void initializeViews() {
        // Initialize views
        editTextUsername = findViewById(R.id.editText_username);
        editTextEmail = findViewById(R.id.editText_email);
        editTextPassword = findViewById(R.id.editText_password);
        editTextConfirmPassword = findViewById(R.id.editText_confirm_password);
        togglePassword = findViewById(R.id.togglePassword);
        toggleConfirmPassword = findViewById(R.id.toggleConfirmPassword);
        checkboxTerms = findViewById(R.id.checkbox_terms);
        btnSignUp = findViewById(R.id.btn_sign_up);
        btnSignUpGoogle = findViewById(R.id.btn_sign_up_google);
        btnSignUpFacebook = findViewById(R.id.btn_sign_up_facebook);
        tvLoginRedirect = findViewById(R.id.tv_login_redirect);

        // Initialize database helper - FIXED: Use the instance variable
        dbHelper = new DatabaseHelper(this);
        dbHelper.logAllUsers();
    }

    private void setupPasswordToggles() {
        // Password toggle for main password field
        togglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePassword.setImageResource(R.drawable.ic_eye_closed);
            } else {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePassword.setImageResource(R.drawable.ic_eye_open);
            }
            isPasswordVisible = !isPasswordVisible;
            editTextPassword.setSelection(editTextPassword.getText().length());
        });

        // Password toggle for confirm password field
        toggleConfirmPassword.setOnClickListener(v -> {
            if (isConfirmPasswordVisible) {
                editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleConfirmPassword.setImageResource(R.drawable.ic_eye_closed);
            } else {
                editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleConfirmPassword.setImageResource(R.drawable.ic_eye_open);
            }
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            editTextConfirmPassword.setSelection(editTextConfirmPassword.getText().length());
        });
    }

    private void setupSignUpLogic() {
        btnSignUp.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString();
            String confirmPassword = editTextConfirmPassword.getText().toString();

            // Debug logging
            Log.d("SignUp", "Username: " + username);
            Log.d("SignUp", "Email: " + email);
            Log.d("SignUp", "Password length: " + password.length());
            Log.d("SignUp", "Terms checked: " + checkboxTerms.isChecked());

            // Validate input fields
            if (!validateInputs(username, email, password, confirmPassword)) {
                Log.d("SignUp", "Validation failed");
                return;
            }

            Log.d("SignUp", "Validation passed");

            // Check if user already exists
            if (dbHelper.checkUserExists(email)) {
                Log.d("SignUp", "User already exists");
                Toast.makeText(this, "Email already registered! Please use a different email.", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("SignUp", "User doesn't exist, attempting to insert");

            // Insert user into database
            boolean isInserted = dbHelper.insertUser(username, email, password);
            Log.d("SignUp", "Insert result: " + isInserted);

            if (isInserted) {
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();

                // Log all users for debugging
                dbHelper.logAllUsers();

                Log.d("SignUp", "About to navigate to Edit_Info");

                // Navigate to Edit_Info activity to complete profile
                Intent intent = new Intent(Sign_up.this, Edit_Info.class);
                intent.putExtra("user_email", email); // Pass email to next activity
                intent.putExtra("username", username); // Pass username to next activity
                startActivity(intent);
                finish(); // Close current activity
            } else {
                Log.d("SignUp", "Insert failed");
                Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs(String username, String email, String password, String confirmPassword) {
        // Check if any field is empty
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate email format (basic validation)
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check password strength (minimum 6 characters)
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if terms are accepted
        if (!checkboxTerms.isChecked()) {
            Toast.makeText(this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void setupLoginRedirect() {
        // FIXED: Redirect to login screen, not Edit_Info
        // Assuming you have a Log_in.class for login functionality
        tvLoginRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(Sign_up.this, Log_in.class); // Changed from Edit_Info to Log_in
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up database helper if needed
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}