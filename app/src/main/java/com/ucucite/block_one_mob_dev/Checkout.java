package com.ucucite.block_one_mob_dev;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;

public class Checkout extends AppCompatActivity {

    private static final String TAG = "Checkout";

    // UI Components
    private ImageView iconBack;
    private ImageView productImage;
    private TextView productName;
    private TextView productBrand;  // Added brand TextView
    private TextView productPrice;
    private TextView productQuantity;
    private TextView subtotal;
    private TextView total;
    private Button btnCheckout;

    // Data
    private Product product;
    private int quantity = 1;
    private final double SHIPPING_FEE = 100.00;
    private DecimalFormat df = new DecimalFormat("₱0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        try {
            // Get product data from intent
            getProductDataFromIntent();

            // Initialize views
            initViews();

            // Setup click listeners
            setupClickListeners();

            // Update UI with product data
            updateProductUI();

            // Calculate and display totals
            calculateTotals();

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading checkout", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void getProductDataFromIntent() {
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

                Log.d(TAG, "Product received: " + (product != null ? product.getName() : "null"));
                Log.d(TAG, "Brand: " + (product != null ? product.getBrand() : "null"));
                Log.d(TAG, "Quantity: " + quantity);

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
            Log.e(TAG, "Error getting product data: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading product data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        try {
            iconBack = findViewById(R.id.icon_back_chkt);
            productImage = findViewById(R.id.product_image);
            productName = findViewById(R.id.product_name);
            productBrand = findViewById(R.id.product_brand);  // Initialize brand TextView
            productPrice = findViewById(R.id.product_price);
            productQuantity = findViewById(R.id.product_quantity);
            subtotal = findViewById(R.id.subtotal);
            total = findViewById(R.id.total);
            btnCheckout = findViewById(R.id.btn_checkout);

        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
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

        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners: " + e.getMessage(), e);
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

            // Here you can implement your checkout logic
            // For now, we'll show a success message and return result to Details activity

            double productPrice = extractPriceValue(product.getPrice());
            double totalAmount = (productPrice * quantity) + SHIPPING_FEE;

            String orderSummary = "Order placed successfully!\n" +
                    "Product: " + product.getName() + "\n" +
                    "Brand: " + product.getBrand() + "\n" +  // Include brand in order summary
                    "Quantity: " + quantity + "\n" +
                    "Total: " + df.format(totalAmount);

            Toast.makeText(this, orderSummary, Toast.LENGTH_LONG).show();

            Log.d(TAG, "Checkout processed for: " + product.getName() + " by " + product.getBrand());

            // Set result to indicate successful checkout
            Intent resultIntent = new Intent();
            resultIntent.putExtra("checkout_success", true);
            resultIntent.putExtra("order_total", totalAmount);
            resultIntent.putExtra("product_name", product.getName());
            resultIntent.putExtra("product_brand", product.getBrand());  // Pass brand back
            setResult(RESULT_OK, resultIntent);

            // Finish this activity and return to Details
            finish();

        } catch (Exception e) {
            Log.e(TAG, "Error processing checkout: " + e.getMessage(), e);
            Toast.makeText(this, "Error processing checkout", Toast.LENGTH_SHORT).show();
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