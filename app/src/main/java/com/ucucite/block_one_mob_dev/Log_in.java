package com.ucucite.block_one_mob_dev;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Log_in extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoogle, btnFacebook, btnSignUp;
    private TextView tvForgotPassword;
    private ImageView ivTogglePassword;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

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
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(Log_in.this, "Please enter email and password!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Log_in.this, "Logging in...", Toast.LENGTH_SHORT).show();

                // ✅ Navigate to nav_bar activity
                Intent intent = new Intent(Log_in.this, nav_bar.class);
                startActivity(intent);
                finish(); // Optional: close the login screen
            }
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
            Toast.makeText(Log_in.this, "Forgot Password Clicked!", Toast.LENGTH_SHORT).show();
        });

        // Sign Up Click
        btnSignUp.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, Sign_up.class);
            startActivity(intent1);
        });

        // Toggle Password Visibility
        ivTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_eye_closed);
                isPasswordVisible = false;
            } else {
                etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_eye_open);
                isPasswordVisible = true;
            }

            etPassword.setSelection(etPassword.getText().length());
        });
    }
}
