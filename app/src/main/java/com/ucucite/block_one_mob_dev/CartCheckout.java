package com.ucucite.block_one_mob_dev;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.os.Parcelable;

public class CartCheckout extends AppCompatActivity {

    private static final String TAG = "CartCheckout";

    // UI Components
    private ImageView iconBack;
    private LinearLayout productsContainer;
    private TextView tvSubtotal;
    private TextView tvTotal;
    private Button btnCheckout;

    // User info UI components (ADD THESE TO YOUR LAYOUT)
    private TextView tvUserName;
    private TextView tvUserPhone;
    private TextView tvUserAddress;

    // Payment method cards
    private CardView cashOnDeliveryCard;
    private CardView bankCardCard;
    private CardView gcashCard;

    // Data
    private CartItem[] cartItems;
    private double subtotal;
    private double shippingFee;
    private double total;
    private String selectedPaymentMethod = "Cash on Delivery"; // Default

    // Database and user session
    private DatabaseHelper databaseHelper;
    private String currentUserEmail;

    private DecimalFormat df = new DecimalFormat("₱0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Log.d(TAG, "onCreate started");

            setContentView(R.layout.activity_cart_checkout);
            Log.d(TAG, "setContentView completed");

            // Initialize database helper
            databaseHelper = new DatabaseHelper(this);

            // Get current user email from SharedPreferences
            getCurrentUserEmail();

            initViews();
            Log.d(TAG, "initViews completed");

            getIntentData();
            Log.d(TAG, "getIntentData completed - cartItems: " + (cartItems != null ? cartItems.length : "null"));

            setupClickListeners();
            Log.d(TAG, "setupClickListeners completed");

            displayCartItems();
            Log.d(TAG, "displayCartItems completed");

            displayUserInfo();
            Log.d(TAG, "displayUserInfo completed");

            updateTotals();
            Log.d(TAG, "updateTotals completed");

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading checkout: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void getCurrentUserEmail() {
        try {
            SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
            currentUserEmail = prefs.getString("user_email", null);
            Log.d(TAG, "Current user email: " + currentUserEmail);

            if (currentUserEmail == null || currentUserEmail.isEmpty()) {
                Toast.makeText(this, "User session not found. Please login again.", Toast.LENGTH_LONG).show();
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting current user email: " + e.getMessage(), e);
        }
    }

    private void initViews() {
        try {
            iconBack = findViewById(R.id.icon_back_chkt);
            productsContainer = findViewById(R.id.products_container);
            tvSubtotal = findViewById(R.id.subtotal);
            tvTotal = findViewById(R.id.total);
            btnCheckout = findViewById(R.id.btn_checkout);

            // User info TextViews (ADD THESE IDs TO YOUR LAYOUT)
            tvUserName = findViewById(R.id.name);
            tvUserPhone = findViewById(R.id.phoneNumber);
            tvUserAddress = findViewById(R.id.address);

            // Payment method cards
            cashOnDeliveryCard = findViewById(R.id.cash_on_delivery_card);
            bankCardCard = findViewById(R.id.bank_card_card);
            gcashCard = findViewById(R.id.gcash_card);

            // Log which views are null
            Log.d(TAG, "View status - iconBack: " + (iconBack != null) +
                    ", productsContainer: " + (productsContainer != null) +
                    ", tvSubtotal: " + (tvSubtotal != null) +
                    ", tvTotal: " + (tvTotal != null) +
                    ", btnCheckout: " + (btnCheckout != null) +
                    ", tvUserName: " + (tvUserName != null) +
                    ", tvUserPhone: " + (tvUserPhone != null) +
                    ", tvUserAddress: " + (tvUserAddress != null) +
                    ", cashOnDeliveryCard: " + (cashOnDeliveryCard != null) +
                    ", bankCardCard: " + (bankCardCard != null) +
                    ", gcashCard: " + (gcashCard != null));

        } catch (Exception e) {
            Log.e(TAG, "Error in initViews: " + e.getMessage(), e);
            throw e;
        }
    }

    private void displayUserInfo() {
        try {
            if (currentUserEmail == null) {
                Log.w(TAG, "Current user email is null, cannot display user info");
                return;
            }

            // Get user info from database
            DatabaseHelper.UserInfo userInfo = databaseHelper.getUserByEmail(currentUserEmail);

            if (userInfo == null) {
                Log.w(TAG, "User info not found for email: " + currentUserEmail);
                Toast.makeText(this, "User information not found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Display user name
            if (tvUserName != null) {
                String fullName = userInfo.getFullName();
                if (fullName.isEmpty()) {
                    tvUserName.setText(userInfo.username != null ? userInfo.username : "Name not set");
                } else {
                    tvUserName.setText(fullName);
                }
            }

            // Display user phone
            if (tvUserPhone != null) {
                if (userInfo.phoneNumber != null && !userInfo.phoneNumber.trim().isEmpty()) {
                    tvUserPhone.setText(userInfo.phoneNumber);
                } else {
                    tvUserPhone.setText("Phone not set");
                    tvUserPhone.setTextColor(Color.parseColor("#FF6B6B")); // Red color to indicate missing info
                }
            }

            // Display user address
            if (tvUserAddress != null) {
                String fullAddress = userInfo.getFullAddress();
                if (!fullAddress.isEmpty()) {
                    tvUserAddress.setText(fullAddress);
                } else {
                    tvUserAddress.setText("Address not set");
                    tvUserAddress.setTextColor(Color.parseColor("#FF6B6B")); // Red color to indicate missing info
                }
            }

            // Check if profile is complete
            boolean isProfileComplete = databaseHelper.isProfileComplete(currentUserEmail);
            if (!isProfileComplete) {
                Toast.makeText(this, "Please complete your profile information before checkout", Toast.LENGTH_LONG).show();
                Log.w(TAG, "User profile is incomplete");
            }

            Log.d(TAG, "User info displayed successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error displaying user info: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading user information", Toast.LENGTH_SHORT).show();
        }
    }

    // ... (rest of your existing methods remain the same)

    private void getIntentData() {
        try {
            Intent intent = getIntent();
            if (intent != null) {
                // Fix: Properly handle Parcelable array conversion
                Parcelable[] parcelableArray = intent.getParcelableArrayExtra("cart_items");
                if (parcelableArray != null) {
                    cartItems = new CartItem[parcelableArray.length];
                    for (int i = 0; i < parcelableArray.length; i++) {
                        cartItems[i] = (CartItem) parcelableArray[i];
                    }
                } else {
                    cartItems = new CartItem[0];
                }

                subtotal = intent.getDoubleExtra("subtotal", 0.0);
                shippingFee = intent.getDoubleExtra("shipping_fee", 100.0);
                total = intent.getDoubleExtra("total", 0.0);

                String checkoutMessage = intent.getStringExtra("checkout_message");
                Log.d(TAG, "Received checkout data: " + checkoutMessage);
                Log.d(TAG, "Cart items count: " + (cartItems != null ? cartItems.length : 0));
                Log.d(TAG, "Subtotal: " + subtotal + ", Total: " + total);
            } else {
                Log.w(TAG, "Intent is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in getIntentData: " + e.getMessage(), e);
            throw e;
        }
    }

    private void setupClickListeners() {
        try {
            // Back button
            if (iconBack != null) {
                iconBack.setOnClickListener(v -> {
                    setResult(RESULT_CANCELED);
                    finish();
                });
            }

            // Checkout button
            if (btnCheckout != null) {
                btnCheckout.setOnClickListener(v -> processCheckout());
            }

            // Payment method selection
            setupPaymentMethodSelection();

        } catch (Exception e) {
            Log.e(TAG, "Error in setupClickListeners: " + e.getMessage(), e);
            throw e;
        }
    }

    private void setupPaymentMethodSelection() {
        try {
            View.OnClickListener paymentClickListener = v -> {
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
            throw e;
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
                selectedCard.setCardBackgroundColor(
                        ContextCompat.getColor(this, R.color.selected_payment_bg));
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
                        String text = textView.getText().toString();
                        if (text.contains("💵") || text.contains("🏦") || text.contains("GCash")) {
                            textView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
                        } else {
                            textView.setTextColor(Color.parseColor("#666666"));
                        }
                    } else if (container.getChildAt(i) instanceof LinearLayout) {
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
            int selectedColor = Color.parseColor("#339967");
            LinearLayout container = (LinearLayout) card.getChildAt(0);
            if (container != null) {
                for (int i = 0; i < container.getChildCount(); i++) {
                    if (container.getChildAt(i) instanceof TextView) {
                        TextView textView = (TextView) container.getChildAt(i);
                        textView.setTextColor(selectedColor);
                    } else if (container.getChildAt(i) instanceof LinearLayout) {
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

    private void displayCartItems() {
        try {
            Log.d(TAG, "Starting displayCartItems");

            if (cartItems == null || cartItems.length == 0) {
                Log.w(TAG, "No cart items to display");
                return;
            }

            if (productsContainer != null) {
                productsContainer.removeAllViews();
                Log.d(TAG, "Cleared existing views from container");
            } else {
                Log.e(TAG, "productsContainer is null!");
                return;
            }

            for (int i = 0; i < cartItems.length; i++) {
                CartItem item = cartItems[i];
                Log.d(TAG, "Processing cart item " + i + ": " + (item != null ? "not null" : "null"));

                if (item != null) {
                    addProductCard(item);
                } else {
                    Log.w(TAG, "Cart item at index " + i + " is null");
                }
            }

            Log.d(TAG, "Displayed " + cartItems.length + " cart items");

        } catch (Exception e) {
            Log.e(TAG, "Error in displayCartItems: " + e.getMessage(), e);
            Toast.makeText(this, "Error displaying cart items", Toast.LENGTH_SHORT).show();
        }
    }

    private void addProductCard(CartItem cartItem) {
        try {
            Log.d(TAG, "Adding product card for: " + (cartItem.getProduct() != null ? cartItem.getProduct().getName() : "null product"));

            View productCard;
            try {
                productCard = getLayoutInflater().inflate(R.layout.item_checkout, productsContainer, false);
            } catch (Exception e) {
                Log.e(TAG, "Error inflating item_checkout layout: " + e.getMessage(), e);
                TextView fallbackView = new TextView(this);
                fallbackView.setText("Product: " + cartItem.getProduct().getName() + " x" + cartItem.getQuantity());
                fallbackView.setPadding(16, 16, 16, 16);
                productsContainer.addView(fallbackView);
                return;
            }

            ImageView productImage = productCard.findViewById(R.id.product_image);
            TextView productName = productCard.findViewById(R.id.product_name);
            TextView productBrand = productCard.findViewById(R.id.product_brand);
            TextView productPrice = productCard.findViewById(R.id.product_price);
            TextView productQuantity = productCard.findViewById(R.id.product_quantity);

            if (cartItem.getProduct() == null) {
                Log.e(TAG, "Product is null for cart item");
                return;
            }

            Product product = cartItem.getProduct();

            if (productName != null && product.getName() != null) {
                productName.setText(product.getName());
            }

            if (productBrand != null && product.getBrand() != null) {
                productBrand.setText(product.getBrand());
            }

            if (productPrice != null && product.getPrice() != null) {
                try {
                    String priceStr = product.getPrice().replaceAll("[₱,]", "").trim();
                    double price = Double.parseDouble(priceStr);
                    productPrice.setText(df.format(price));
                } catch (NumberFormatException e) {
                    productPrice.setText(product.getPrice());
                    Log.w(TAG, "Failed to parse price: " + product.getPrice());
                }
            }

            if (productImage != null && product.getImageResource() != 0) {
                productImage.setImageResource(product.getImageResource());
            }

            if (productQuantity != null) {
                productQuantity.setText("x" + cartItem.getQuantity());
            }

            if (productsContainer != null) {
                productsContainer.addView(productCard);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) productCard.getLayoutParams();
                if (params != null) {
                    params.bottomMargin = (int) (8 * getResources().getDisplayMetrics().density);
                    productCard.setLayoutParams(params);
                }
            }

            Log.d(TAG, "Successfully added product card");

        } catch (Exception e) {
            Log.e(TAG, "Error in addProductCard: " + e.getMessage(), e);
            TextView fallbackView = new TextView(this);
            fallbackView.setText("Error loading product");
            fallbackView.setPadding(16, 16, 16, 16);
            if (productsContainer != null) {
                productsContainer.addView(fallbackView);
            }
        }
    }

    private void updateTotals() {
        try {
            if (tvSubtotal != null) {
                tvSubtotal.setText(df.format(subtotal));
            }

            if (tvTotal != null) {
                tvTotal.setText(df.format(total));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in updateTotals: " + e.getMessage(), e);
        }
    }

    private void processCheckout() {
        try {
            if (cartItems == null || cartItems.length == 0) {
                Toast.makeText(this, "No items to checkout!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if user profile is complete before allowing checkout
            if (currentUserEmail != null && !databaseHelper.isProfileComplete(currentUserEmail)) {
                Toast.makeText(this, "Please complete your profile information first", Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(this, "Processing order with " + selectedPaymentMethod + "...", Toast.LENGTH_SHORT).show();

            Log.d(TAG, "Processing checkout:");
            Log.d(TAG, "Payment method: " + selectedPaymentMethod);
            Log.d(TAG, "Items: " + cartItems.length);
            Log.d(TAG, "Subtotal: " + df.format(subtotal));
            Log.d(TAG, "Shipping: " + df.format(shippingFee));
            Log.d(TAG, "Total: " + df.format(total));

            if (btnCheckout != null) {
                btnCheckout.postDelayed(() -> {
                    try {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("checkout_success", true);
                        resultIntent.putExtra("payment_method", selectedPaymentMethod);
                        resultIntent.putExtra("order_total", total);

                        setResult(RESULT_OK, resultIntent);

                        Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show();
                        finish();
                    } catch (Exception e) {
                        Log.e(TAG, "Error in checkout completion: " + e.getMessage(), e);
                        Toast.makeText(this, "Error completing order", Toast.LENGTH_SHORT).show();
                    }
                }, 1500);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error processing checkout: " + e.getMessage(), e);
            Toast.makeText(this, "Error processing order. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}