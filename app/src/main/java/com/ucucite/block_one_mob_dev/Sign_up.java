package com.ucucite.block_one_mob_dev;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;
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

        btnSignUp.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString();
            String confirmPassword = editTextConfirmPassword.getText().toString();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!checkboxTerms.isChecked()) {
                Toast.makeText(this, "Please accept the terms", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.checkUserExists(email)) {
                Toast.makeText(this, "Email already registered!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean inserted = dbHelper.insertUser(username, email, password);
            if (inserted) {
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                dbHelper.logAllUsers();

                Intent intent = new Intent(Sign_up.this, Edit_Info.class);
                intent.putExtra("email", email); // Pass email to Edit_Info
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Something went wrong. Try again.", Toast.LENGTH_SHORT).show();
            }
        });

        tvLoginRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(Sign_up.this, Log_in.class);
            startActivity(intent);
            finish();
        });
    }
}
