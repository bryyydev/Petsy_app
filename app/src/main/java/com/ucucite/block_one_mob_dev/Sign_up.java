package com.ucucite.block_one_mob_dev;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Sign_up extends AppCompatActivity {

    private EditText editTextUsername, editTextEmail, editTextPassword, editTextConfirmPassword;
    private ImageView togglePassword, toggleConfirmPassword;
    private CheckBox checkboxTerms;
    private Button btnSignUp;
    private LinearLayout btnSignUpGoogle, btnSignUpFacebook;
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
        setupSocialButtons();
    }

    private void initializeViews() {
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

        dbHelper = new DatabaseHelper(this);
        dbHelper.logAllUsers();
    }

    private void setupPasswordToggles() {
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

            if (!validateInputs(username, email, password, confirmPassword)) return;

            if (dbHelper.checkUserExists(email)) {
                Toast.makeText(this, "Email already registered! Please use a different email.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isInserted = dbHelper.insertUser(username, email, password);

            if (isInserted) {
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                dbHelper.logAllUsers();

                Intent intent = new Intent(Sign_up.this, Edit_Info.class);
                intent.putExtra("user_email", email);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs(String username, String email, String password, String confirmPassword) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!checkboxTerms.isChecked()) {
            Toast.makeText(this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void setupLoginRedirect() {
        tvLoginRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(Sign_up.this, Log_in.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupSocialButtons() {
        btnSignUpGoogle.setOnClickListener(v ->
                Toast.makeText(this, "Google Sign-Up Clicked!", Toast.LENGTH_SHORT).show()
        );

        btnSignUpFacebook.setOnClickListener(v ->
                Toast.makeText(this, "Facebook Sign-Up Clicked!", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
