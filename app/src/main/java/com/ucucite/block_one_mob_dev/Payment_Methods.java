package com.ucucite.block_one_mob_dev; // Replace with your actual package name

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Payment_Methods extends AppCompatActivity {

    // Views
    private LinearLayout addGcashButton, addCardButton;
    private LinearLayout bottomSheetGcash, bottomSheetCard;
    private View overlay;
    private ImageView backArrow;

    // GCash Bottom Sheet
    private EditText etGcashNumber;
    private TextView btnCancelGcash, btnAddGcash;

    // Card Bottom Sheet
    private EditText etCardHolder, etCardNumber, etExpiryDate, etCvv;
    private TextView btnSaveCard;

    private boolean isBottomSheetVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods); // Replace with your actual layout name

        initializeViews();
        setupClickListeners();
        setupTextWatchers();
    }

    private void initializeViews() {
        // Main buttons
        addGcashButton = findViewById(R.id.add_gcash_button);
        addCardButton = findViewById(R.id.add_card_button);
        backArrow = findViewById(R.id.imageView3);

        // Common elements
        overlay = findViewById(R.id.overlay);

        // GCash bottom sheet
        bottomSheetGcash = findViewById(R.id.bottom_sheet_gcash);
        etGcashNumber = findViewById(R.id.et_gcash_number);
        btnCancelGcash = findViewById(R.id.btn_cancel_gcash);
        btnAddGcash = findViewById(R.id.btn_add_gcash);

        // Card bottom sheet
        bottomSheetCard = findViewById(R.id.bottom_sheet_card);
        etCardHolder = findViewById(R.id.et_card_holder);
        etCardNumber = findViewById(R.id.et_card_number);
        etExpiryDate = findViewById(R.id.et_expiry_date);
        etCvv = findViewById(R.id.et_cvv);
        btnSaveCard = findViewById(R.id.btn_save_card);
    }

    private void setupClickListeners() {
        // GCash add button click
        addGcashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGcashBottomSheet();
            }
        });

        // Card add button click
        addCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCardBottomSheet();
            }
        });

        // GCash bottom sheet buttons
        btnCancelGcash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBottomSheet();
            }
        });

        btnAddGcash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGcashNumber();
            }
        });

        // Card bottom sheet save button
        btnSaveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCard();
            }
        });

        // Overlay click to dismiss
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBottomSheet();
            }
        });

        // Back arrow click
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupTextWatchers() {
        // Card number formatting (add spaces every 4 digits)
        etCardNumber.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;

                isFormatting = true;
                String original = s.toString().replaceAll("\\s", "");
                StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < original.length(); i++) {
                    if (i > 0 && i % 4 == 0) {
                        formatted.append(" ");
                    }
                    formatted.append(original.charAt(i));
                }

                s.replace(0, s.length(), formatted.toString());
                isFormatting = false;
            }
        });

        // Expiry date formatting (MM/YY)
        etExpiryDate.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;

                isFormatting = true;
                String original = s.toString().replaceAll("/", "");

                if (original.length() >= 2) {
                    String formatted = original.substring(0, 2) + "/" + original.substring(2);
                    s.replace(0, s.length(), formatted);
                }

                isFormatting = false;
            }
        });
    }

    private void showGcashBottomSheet() {
        if (isBottomSheetVisible) return;

        showBottomSheet(bottomSheetGcash);

        // Clear previous input
        etGcashNumber.setText("");
    }

    private void showCardBottomSheet() {
        if (isBottomSheetVisible) return;

        showBottomSheet(bottomSheetCard);

        // Clear previous inputs
        etCardHolder.setText("");
        etCardNumber.setText("");
        etExpiryDate.setText("");
        etCvv.setText("");
    }

    private void showBottomSheet(LinearLayout bottomSheet) {
        isBottomSheetVisible = true;

        // Show overlay and bottom sheet
        overlay.setVisibility(View.VISIBLE);
        bottomSheet.setVisibility(View.VISIBLE);

        // Animate overlay fade in
        overlay.setAlpha(0f);
        overlay.animate()
                .alpha(1f)
                .setDuration(300)
                .start();

        // Animate bottom sheet slide up
        ObjectAnimator slideUp = ObjectAnimator.ofFloat(bottomSheet, "translationY", 500f, 0f);
        slideUp.setDuration(300);
        slideUp.setInterpolator(new DecelerateInterpolator());
        slideUp.start();
    }

    private void hideBottomSheet() {
        if (!isBottomSheetVisible) return;

        // Find which bottom sheet is currently visible
        LinearLayout visibleBottomSheet = null;
        if (bottomSheetGcash.getVisibility() == View.VISIBLE) {
            visibleBottomSheet = bottomSheetGcash;
        } else if (bottomSheetCard.getVisibility() == View.VISIBLE) {
            visibleBottomSheet = bottomSheetCard;
        }

        if (visibleBottomSheet == null) return;

        final LinearLayout bottomSheetToHide = visibleBottomSheet;

        // Animate overlay fade out
        overlay.animate()
                .alpha(0f)
                .setDuration(250)
                .start();

        // Animate bottom sheet slide down
        ObjectAnimator slideDown = ObjectAnimator.ofFloat(bottomSheetToHide, "translationY", 0f, 500f);
        slideDown.setDuration(250);
        slideDown.setInterpolator(new DecelerateInterpolator());
        slideDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                bottomSheetToHide.setVisibility(View.GONE);
                overlay.setVisibility(View.GONE);
                isBottomSheetVisible = false;
            }
        });
        slideDown.start();
    }

    private void addGcashNumber() {
        String phoneNumber = etGcashNumber.getText().toString().trim();

        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate phone number format (basic validation)
        if (!isValidPhoneNumber(phoneNumber)) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the GCash number
        saveGcashNumber(phoneNumber);

        Toast.makeText(this, "GCash number added successfully", Toast.LENGTH_SHORT).show();
        hideBottomSheet();
    }

    private void saveCard() {
        String cardHolder = etCardHolder.getText().toString().trim();
        String cardNumber = etCardNumber.getText().toString().replaceAll("\\s", "");
        String expiryDate = etExpiryDate.getText().toString().trim();
        String cvv = etCvv.getText().toString().trim();

        // Validate inputs
        if (cardHolder.isEmpty()) {
            Toast.makeText(this, "Please enter card holder name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cardNumber.isEmpty() || cardNumber.length() < 13) {
            Toast.makeText(this, "Please enter a valid card number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (expiryDate.isEmpty() || !expiryDate.matches("\\d{2}/\\d{2}")) {
            Toast.makeText(this, "Please enter valid expiry date (MM/YY)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cvv.isEmpty() || cvv.length() < 3) {
            Toast.makeText(this, "Please enter a valid CVV", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the card
        saveCardDetails(cardHolder, cardNumber, expiryDate, cvv);

        Toast.makeText(this, "Card added successfully", Toast.LENGTH_SHORT).show();
        hideBottomSheet();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Basic Philippine phone number validation
        String cleanNumber = phoneNumber.replaceAll("[\\s\\-\\(\\)]", "");

        return cleanNumber.matches("^(\\+63|63|09)\\d{9}$") ||
                cleanNumber.matches("^09\\d{9}$") ||
                cleanNumber.matches("^\\d{11}$");
    }

    private void saveGcashNumber(String phoneNumber) {
        // Implement your save logic here
        System.out.println("GCash number saved: " + phoneNumber);

        // Example implementations:
        // SharedPreferences prefs = getSharedPreferences("PaymentMethods", MODE_PRIVATE);
        // prefs.edit().putString("gcash_number", phoneNumber).apply();
    }

    private void saveCardDetails(String cardHolder, String cardNumber, String expiryDate, String cvv) {
        // Implement your save logic here
        System.out.println("Card saved: " + cardHolder + ", " +
                cardNumber.substring(0, 4) + "****" + cardNumber.substring(cardNumber.length()-4));

        // Example implementations:
        // Save to database, SharedPreferences, or send to server
        // NOTE: In production, never store full card details locally for security reasons
    }

    @Override
    public void onBackPressed() {
        if (isBottomSheetVisible) {
            hideBottomSheet();
        } else {
            super.onBackPressed();
        }
    }
}