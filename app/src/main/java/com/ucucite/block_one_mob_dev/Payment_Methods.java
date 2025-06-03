package com.ucucite.block_one_mob_dev;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class Payment_Methods extends AppCompatActivity {

    Button addCardBtn, addGcashBtn;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);

        // Initialize views
        addCardBtn = findViewById(R.id.add_card_button);
        addGcashBtn = findViewById(R.id.add_gcash_button);
        backButton = findViewById(R.id.imageView3);

        // Set click listeners
        addCardBtn.setOnClickListener(v -> showAddCardSheet());
        addGcashBtn.setOnClickListener(v -> showAddNumberSheet());

        // Back button click listener
        backButton.setOnClickListener(v -> {
            // Finish the current activity to return to profile_fragment
            finish();
        });
    }

    private void showAddCardSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_add_card, null);
        bottomSheetDialog.setContentView(view);

        EditText cardHolderInput = view.findViewById(R.id.card_holder_input);
        EditText cardNumberInput = view.findViewById(R.id.card_number_input);
        EditText validInput = view.findViewById(R.id.valid_input);
        Button saveBtn = view.findViewById(R.id.save_card_btn);

        saveBtn.setOnClickListener(v -> {
            String cardHolder = cardHolderInput.getText().toString().trim();
            String cardNumber = cardNumberInput.getText().toString().trim();
            String validDate = validInput.getText().toString().trim();

            if (!cardHolder.isEmpty() && cardNumber.length() >= 4 && !validDate.isEmpty()) {
                addCardToUI(cardHolder, cardNumber, validDate);
                bottomSheetDialog.dismiss();
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetDialog.show();
    }

    private void addCardToUI(String holderName, String cardNumber, String validDate) {
        LinearLayout cardContainer = findViewById(R.id.cardContainer);

        View cardView = LayoutInflater.from(this).inflate(R.layout.item_card, null);

        TextView cardNumberText = cardView.findViewById(R.id.card_number);
        TextView holderText = cardView.findViewById(R.id.card_holder);
        TextView expiryText = cardView.findViewById(R.id.expiry_date);

        String lastFour = cardNumber.length() >= 4
                ? cardNumber.substring(cardNumber.length() - 4)
                : cardNumber;

        cardNumberText.setText("**** **** **** " + lastFour);
        holderText.setText(holderName.toUpperCase());
        expiryText.setText(validDate);

        // Add the card view right before the "+" button layout
        cardContainer.addView(cardView, 1);
    }

    private void showAddNumberSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_add_number, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }
}