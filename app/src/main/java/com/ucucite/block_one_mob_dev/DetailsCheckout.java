package com.ucucite.block_one_mob_dev;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import java.text.DecimalFormat;

public class DetailsCheckout extends AppCompatActivity {

    private static final String TAG = "Checkout";

    // UI Components
    private ImageView iconBack;
    private ImageView productImage;
    private TextView productName;
    private TextView productBrand;
    private TextView productPrice;
    private TextView productQuantity;
    private TextView subtotal;
    private TextView total;
    private Button btnCheckout;

    // User information UI components
    private TextView fullName;
    private TextView fullAddress;
    private TextView phoneNumber;

    // Payment method cards
    private CardView cashOnDeliveryCard;
    private CardView bankCardCard;
    private CardView gcashCard;

    // Data
    private Product product;
    private int quantity = 1;
    private final double SHIPPING_FEE = 100.00;
    private String selectedPaymentMethod = "Cash on Delivery"; // Default
    private DecimalFormat df = new DecimalFormat("₱0.00");

    // User data variables
    private String userEmail;
    private String userStreetBarangay;
    private String userMunicipalityProvince;
    private String userFullName;
    private String userPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_checkout);

        try {
            // Get product data and user data from intent
            getDataFromIntent();

            // Initialize views
            initViews();

            // Setup click listeners
            setupClickListeners();

            // Update UI with product data
            updateProductUI();

            // Update UI with user data
            updateUserUI();

            // Calculate and display totals
            calculateTotals();

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading checkout", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void getDataFromIntent() {
        try {
            Intent intent = getIntent();
            if (intent != null) {
                // Get product from intent
                if (intent.hasExtra("single_product")) {
                    product = intent.getParcelableExtra("single_product");
                }

                // Get quantity
                if (intent.hasExtra("product_quantity")) {
                    quantity = intent.getIntExtra("product_quantity", 1);
                }

                // Get user data from intent
                userEmail = intent.getStringExtra("user_email");
                userStreetBarangay = intent.getStringExtra("user_street_barangay");
                userMunicipalityProvince = intent.getStringExtra("user_municipality_province");
                userFullName = intent.getStringExtra("user_full_name");
                userPhoneNumber = intent.getStringExtra("user_phone_number");

                Log.d(TAG, "Product received: " + (product != null ? product.getName() : "null"));
                Log.d(TAG, "Brand: " + (product != null ? product.getBrand() : "null"));
                Log.d(TAG, "Quantity: " + quantity);
                Log.d(TAG, "User data received in Checkout - Email: " + userEmail +
                        ", Street/Barangay: " + userStreetBarangay +
                        ", Municipality/Province: " + userMunicipalityProvince +
                        ", Full Name: " + userFullName +
                        ", Phone: " + userPhoneNumber);

                if (product == null) {
                    Log.e(TAG, "Product is null after retrieval");
                    Toast.makeText(this, "Product data not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Log.e(TAG, "Intent is null");
                Toast.makeText(this, "No intent data", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting data from intent: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        try {
            iconBack = findViewById(R.id.icon_back_chkt);
            productImage = findViewById(R.id.product_image);
            productName = findViewById(R.id.product_name);
            productBrand = findViewById(R.id.product_brand);
            productPrice = findViewById(R.id.product_price);
            productQuantity = findViewById(R.id.product_quantity);
            subtotal = findViewById(R.id.subtotal);
            total = findViewById(R.id.total);
            btnCheckout = findViewById(R.id.btn_checkout);

            // Initialize user information views
            fullName = findViewById(R.id.fullname);
            fullAddress = findViewById(R.id.full_address);
            phoneNumber = findViewById(R.id.phoneNumber);

            // Payment method cards
            cashOnDeliveryCard = findViewById(R.id.cash_on_delivery_card);
            bankCardCard = findViewById(R.id.bank_card_card);
            gcashCard = findViewById(R.id.gcash_card);

            // Log which payment method views are found
            Log.d(TAG, "Payment method views - COD: " + (cashOnDeliveryCard != null) +
                    ", Bank Card: " + (bankCardCard != null) +
                    ", GCash: " + (gcashCard != null));

        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
        }
    }

    private void updateUserUI() {
        try {
            // Update full name
            if (fullName != null) {
                if (userFullName != null && !userFullName.isEmpty()) {
                    fullName.setText(userFullName);
                } else {
                    fullName.setText("Jaime Yee II"); // Default fallback
                }
            }

            // Update full address
            if (fullAddress != null) {
                if (userStreetBarangay != null && userMunicipalityProvince != null) {
                    String completeAddress = userStreetBarangay + "\n" + userMunicipalityProvince;
                    fullAddress.setText(completeAddress);
                } else {
                    fullAddress.setText("Block 2 Lot 17 Estrella Homes Phase 1\nToclong Kawit Cavite"); // Default fallback
                }
            }

            // Update phone number
            if (phoneNumber != null) {
                if (userPhoneNumber != null && !userPhoneNumber.isEmpty()) {
                    phoneNumber.setText(userPhoneNumber);
                } else {
                    phoneNumber.setText("+639155371154"); // Default fallback
                }
            }

            Log.d(TAG, "User UI updated successfully");
            Log.d(TAG, "Displayed - Name: " + (fullName != null ? fullName.getText() : "null") +
                    ", Phone: " + (phoneNumber != null ? phoneNumber.getText() : "null"));

        } catch (Exception e) {
            Log.e(TAG, "Error updating user UI: " + e.getMessage(), e);
        }
    }

    private void setupClickListeners() {
        try {
            // Back button click listener
            if (iconBack != null) {
                iconBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            }

            // Checkout button click listener
            if (btnCheckout != null) {
                btnCheckout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        processCheckout();
                    }
                });
            }

            // Setup payment method selection
            setupPaymentMethodSelection();

        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners: " + e.getMessage(), e);
        }
    }

    private void setupPaymentMethodSelection() {
        try {
            View.OnClickListener paymentClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        // Reset all cards to default state
                        resetPaymentMethodCards();

                        if (v == cashOnDeliveryCard) {
                            selectedPaymentMethod = "Cash on Delivery";
                            highlightSelectedPaymentMethod(cashOnDeliveryCard);
                        } else if (v == bankCardCard) {
                            selectedPaymentMethod = "Bank Card";
                            highlightSelectedPaymentMethod(bankCardCard);
                        } else if (v == gcashCard) {
                            selectedPaymentMethod = "GCash";
                            highlightSelectedPaymentMethod(gcashCard);
                        }

                        Log.d(TAG, "Selected payment method: " + selectedPaymentMethod);
                    } catch (Exception e) {
                        Log.e(TAG, "Error in payment method selection: " + e.getMessage(), e);
                    }
                }
            };

            if (cashOnDeliveryCard != null) {
                cashOnDeliveryCard.setOnClickListener(paymentClickListener);
                // Set as default selected
                highlightSelectedPaymentMethod(cashOnDeliveryCard);
            }

            if (bankCardCard != null) {
                bankCardCard.setOnClickListener(paymentClickListener);
            }

            if (gcashCard != null) {
                gcashCard.setOnClickListener(paymentClickListener);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in setupPaymentMethodSelection: " + e.getMessage(), e);
        }
    }

    private void resetPaymentMethodCards() {
        try {
            float defaultElevation = 4 * getResources().getDisplayMetrics().density;

            if (cashOnDeliveryCard != null) {
                cashOnDeliveryCard.setCardElevation(defaultElevation);
                cashOnDeliveryCard.setCardBackgroundColor(
                        ContextCompat.getColor(this, android.R.color.white));
                resetCardTextColors(cashOnDeliveryCard);
            }
            if (bankCardCard != null) {
                bankCardCard.setCardElevation(defaultElevation);
                bankCardCard.setCardBackgroundColor(
                        ContextCompat.getColor(this, android.R.color.white));
                resetCardTextColors(bankCardCard);
            }
            if (gcashCard != null) {
                gcashCard.setCardElevation(defaultElevation);
                gcashCard.setCardBackgroundColor(
                        ContextCompat.getColor(this, android.R.color.white));
                resetCardTextColors(gcashCard);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in resetPaymentMethodCards: " + e.getMessage(), e);
        }
    }

    private void highlightSelectedPaymentMethod(CardView selectedCard) {
        try {
            if (selectedCard != null) {
                float highlightElevation = 8 * getResources().getDisplayMetrics().density;
                selectedCard.setCardElevation(highlightElevation);

                // Use the selected payment background color
                selectedCard.setCardBackgroundColor(
                        ContextCompat.getColor(this, R.color.selected_payment_bg));

                // Change text colors to the selected color #339967
                setCardTextColors(selectedCard);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in highlightSelectedPaymentMethod: " + e.getMessage(), e);
        }
    }

    private void resetCardTextColors(CardView card) {
        try {
            LinearLayout container = (LinearLayout) card.getChildAt(0);
            if (container != null) {
                for (int i = 0; i < container.getChildCount(); i++) {
                    if (container.getChildAt(i) instanceof TextView) {
                        TextView textView = (TextView) container.getChildAt(i);
                        // Reset to original colors based on text content
                        String text = textView.getText().toString();
                        if (text.contains("💵") || text.contains("🏦") || text.contains("GCash")) {
                            // Title text - black
                            textView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
                        } else {
                            // Description text - gray
                            textView.setTextColor(Color.parseColor("#666666"));
                        }
                    } else if (container.getChildAt(i) instanceof LinearLayout) {
                        // Handle nested LinearLayouts (like in GCash card)
                        LinearLayout nestedLayout = (LinearLayout) container.getChildAt(i);
                        for (int j = 0; j < nestedLayout.getChildCount(); j++) {
                            if (nestedLayout.getChildAt(j) instanceof TextView) {
                                TextView textView = (TextView) nestedLayout.getChildAt(j);
                                String text = textView.getText().toString();
                                if (text.equals("GCash")) {
                                    textView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
                                } else {
                                    textView.setTextColor(Color.parseColor("#666666"));
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in resetCardTextColors: " + e.getMessage(), e);
        }
    }

    private void setCardTextColors(CardView card) {
        try {
            // Define the selected color #339967
            int selectedColor = Color.parseColor("#339967");

            LinearLayout container = (LinearLayout) card.getChildAt(0);
            if (container != null) {
                for (int i = 0; i < container.getChildCount(); i++) {
                    if (container.getChildAt(i) instanceof TextView) {
                        TextView textView = (TextView) container.getChildAt(i);
                        textView.setTextColor(selectedColor);
                    } else if (container.getChildAt(i) instanceof LinearLayout) {
                        // Handle nested LinearLayouts (like in GCash card)
                        LinearLayout nestedLayout = (LinearLayout) container.getChildAt(i);
                        for (int j = 0; j < nestedLayout.getChildCount(); j++) {
                            if (nestedLayout.getChildAt(j) instanceof TextView) {
                                TextView textView = (TextView) nestedLayout.getChildAt(j);
                                textView.setTextColor(selectedColor);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in setCardTextColors: " + e.getMessage(), e);
        }
    }

    private void updateProductUI() {
        try {
            if (product == null) {
                Log.e(TAG, "Product is null, cannot update UI");
                return;
            }

            // Update product image
            if (productImage != null) {
                productImage.setImageResource(product.getImageResource());
            }

            // Update product name
            if (productName != null) {
                productName.setText(product.getName());
            }

            // Update product brand
            if (productBrand != null) {
                productBrand.setText(product.getBrand());
            }

            // Update product price
            if (productPrice != null) {
                productPrice.setText(product.getPrice());
            }

            // Update quantity
            if (productQuantity != null) {
                productQuantity.setText("x" + quantity);
            }

            Log.d(TAG, "Product UI updated successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error updating product UI: " + e.getMessage(), e);
        }
    }

    private void calculateTotals() {
        try {
            if (product == null) {
                Log.e(TAG, "Product is null, cannot calculate totals");
                return;
            }

            // Extract numeric value from price string (assuming format like "₱ 220.00")
            double productPrice = extractPriceValue(product.getPrice());
            double subtotalAmount = productPrice * quantity;
            double totalAmount = subtotalAmount + SHIPPING_FEE;

            // Update subtotal
            if (subtotal != null) {
                subtotal.setText(df.format(subtotalAmount));
            }

            // Update total
            if (total != null) {
                total.setText(df.format(totalAmount));
            }

            Log.d(TAG, "Totals calculated - Subtotal: " + subtotalAmount + ", Total: " + totalAmount);

        } catch (Exception e) {
            Log.e(TAG, "Error calculating totals: " + e.getMessage(), e);
        }
    }

    private double extractPriceValue(String priceString) {
        try {
            if (priceString == null || priceString.isEmpty()) {
                return 0.0;
            }

            // Remove currency symbol and spaces, then parse
            String numericPrice = priceString.replaceAll("[₱\\s,]", "");
            return Double.parseDouble(numericPrice);

        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing price: " + priceString + " - " + e.getMessage());
            return 0.0;
        }
    }

    private void processCheckout() {
        try {
            if (product == null) {
                Toast.makeText(this, "Product data not available", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate user data before processing
            if (userEmail == null || userEmail.isEmpty()) {
                Toast.makeText(this, "User email is required for checkout", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "User email is missing");
                return;
            }

            // Show processing message with selected payment method
            Toast.makeText(this, "Processing order with " + selectedPaymentMethod + "...", Toast.LENGTH_SHORT).show();

            double productPrice = extractPriceValue(product.getPrice());
            double totalAmount = (productPrice * quantity) + SHIPPING_FEE;

            Log.d(TAG, "Processing checkout:");
            Log.d(TAG, "Payment method: " + selectedPaymentMethod);
            Log.d(TAG, "Product: " + product.getName());
            Log.d(TAG, "Brand: " + product.getBrand());
            Log.d(TAG, "Quantity: " + quantity);
            Log.d(TAG, "Total: " + df.format(totalAmount));
            Log.d(TAG, "Delivery to: " + userStreetBarangay + ", " + userMunicipalityProvince);
            Log.d(TAG, "Customer: " + userFullName);
            Log.d(TAG, "Customer email: " + userEmail);
            Log.d(TAG, "Customer phone: " + userPhoneNumber);

            // Simulate processing delay
            if (btnCheckout != null) {
                btnCheckout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String orderSummary = "Order placed successfully!\n" +
                                    "Product: " + product.getName() + "\n" +
                                    "Brand: " + product.getBrand() + "\n" +
                                    "Quantity: " + quantity + "\n" +
                                    "Payment: " + selectedPaymentMethod + "\n" +
                                    "Total: " + df.format(totalAmount) + "\n" +
                                    "Customer: " + userFullName + "\n" +
                                    "Phone: " + userPhoneNumber + "\n" +
                                    "Delivery to: " + userStreetBarangay + ", " + userMunicipalityProvince;

                            Toast.makeText(DetailsCheckout.this, orderSummary, Toast.LENGTH_LONG).show();

                            // Set result to indicate successful checkout
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("checkout_success", true);
                            resultIntent.putExtra("payment_method", selectedPaymentMethod);
                            resultIntent.putExtra("order_total", totalAmount);
                            resultIntent.putExtra("product_name", product.getName());
                            resultIntent.putExtra("product_brand", product.getBrand());
                            resultIntent.putExtra("customer_name", userFullName);
                            resultIntent.putExtra("customer_email", userEmail);
                            resultIntent.putExtra("customer_phone", userPhoneNumber);
                            resultIntent.putExtra("delivery_address", userStreetBarangay + ", " + userMunicipalityProvince);
                            setResult(RESULT_OK, resultIntent);

                            // Finish this activity and return to Details
                            finish();
                        } catch (Exception e) {
                            Log.e(TAG, "Error in checkout completion: " + e.getMessage(), e);
                            Toast.makeText(DetailsCheckout.this, "Error completing order", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 1500);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error processing checkout: " + e.getMessage(), e);
            Toast.makeText(this, "Error processing checkout. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
        } catch (Exception e) {
            Log.e(TAG, "Error on back pressed: " + e.getMessage(), e);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Checkout activity destroyed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Checkout activity paused");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Checkout activity resumed");
    }
}