package com.ucucite.block_one_mob_dev;

// Payment_Methods.java (matching your class name)
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class Payment_Methods extends AppCompatActivity {

    private LinearLayout addCardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods); // or your layout name

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        // Find the LinearLayout that contains the "+" text
        addCardButton = findViewById(R.id.add_card_button); // You'll need to add this ID
    }

    private void setupClickListeners() {
        addCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCardBottomSheet();
            }
        });
    }

    private void showAddCardBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.activity_add_payment_method, null);

        // Initialize views
        EditText etCardHolder = view.findViewById(R.id.et_card_holder);
        EditText etCardNumber = view.findViewById(R.id.et_card_number);
        EditText etValidDate = view.findViewById(R.id.et_valid);
        EditText etCvv = view.findViewById(R.id.et_cvv);
        Button btnSaveChanges = view.findViewById(R.id.btn_save_changes);

        // Setup card number formatting
        setupCardNumberFormatting(etCardNumber);

        // Setup date formatting (MM/YY)
        setupDateFormatting(etValidDate);

        // Setup save button click
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cardHolder = etCardHolder.getText().toString().trim();
                String cardNumber = etCardNumber.getText().toString().replace(" ", "").trim();
                String validDate = etValidDate.getText().toString().trim();
                String cvv = etCvv.getText().toString().trim();

                if (validateCardDetails(cardHolder, cardNumber, validDate, cvv)) {
                    saveCard(cardHolder, cardNumber, validDate, cvv);
                    bottomSheetDialog.dismiss();
                    Toast.makeText(Payment_Methods.this, "Card added successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void setupCardNumberFormatting(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;

                isFormatting = true;
                String text = s.toString().replace(" ", "");
                StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < text.length(); i++) {
                    if (i > 0 && i % 4 == 0) {
                        formatted.append(" ");
                    }
                    formatted.append(text.charAt(i));
                }

                editText.setText(formatted.toString());
                editText.setSelection(formatted.length());
                isFormatting = false;
            }
        });
    }

    private void setupDateFormatting(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;

                isFormatting = true;
                String text = s.toString().replace("/", "");

                if (text.length() >= 2) {
                    String month = text.substring(0, 2);
                    String year = text.length() > 2 ? text.substring(2, Math.min(text.length(), 4)) : "";
                    String formatted = month + (year.isEmpty() ? "" : "/" + year);

                    editText.setText(formatted);
                    editText.setSelection(formatted.length());
                }

                isFormatting = false;
            }
        });
    }

    private boolean validateCardDetails(String holder, String number, String date, String cvv) {
        if (holder.isEmpty()) {
            Toast.makeText(this, "Card holder name is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (number.length() < 16) {
            Toast.makeText(this, "Invalid card number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (date.length() != 5 || !date.contains("/")) {
            Toast.makeText(this, "Invalid date format (MM/YY)", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (cvv.length() < 3) {
            Toast.makeText(this, "Invalid CVV", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveCard(String holder, String number, String date, String cvv) {
        // Implement your card saving logic here
        // This could involve saving to database, shared preferences, or API call
        Log.d("PaymentMethods", "Card saved: " + holder + ", " + number.substring(number.length() - 4));

        // Example: Save to SharedPreferences
        // SharedPreferences prefs = getSharedPreferences("cards", MODE_PRIVATE);
        // SharedPreferences.Editor editor = prefs.edit();
        // editor.putString("card_holder", holder);
        // editor.putString("card_last_four", number.substring(number.length() - 4));
        // editor.apply();
    }
}