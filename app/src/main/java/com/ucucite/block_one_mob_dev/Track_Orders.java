package com.ucucite.block_one_mob_dev;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Track_Orders extends AppCompatActivity {

    private ImageView backIcon;
    private ImageView productImage;
    private TextView productName;
    private TextView brandName;
    private TextView orderID;
    private TextView fromAddress;
    private TextView toAddress;
    private TextView packageWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_orders); // Make sure this matches your XML filename

        // Initialize views
        initViews();

        // Set up back button
        setupBackButton();

        // Get data from intent and populate views
        populateOrderData();
    }

    private void initViews() {
        backIcon = findViewById(R.id.icon_back_chkt);
        productImage = findViewById(R.id.product_image);
        productName = findViewById(R.id.product_name);
        brandName = findViewById(R.id.brand_name);
        orderID = findViewById(R.id.order_id);
        fromAddress = findViewById(R.id.from_address);
        toAddress = findViewById(R.id.address);
        packageWeight = findViewById(R.id.package_weight);
    }

    private void setupBackButton() {
        if (backIcon != null) {
            backIcon.setOnClickListener(v -> {
                onBackPressed(); // Go back to previous screen
            });
        }
    }

    private void populateOrderData() {
        // Get data from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Set product name
            String productNameStr = extras.getString("product_name", "Cooked Dog Food");
            if (productName != null) {
                productName.setText(productNameStr);
            }

            // Set brand name with product details
            String brandNameStr = extras.getString("product_brand", "Chicken and White Rice");
            int quantity = extras.getInt("product_quantity", 1);
            if (brandName != null) {
                // Combine brand with quantity info
                brandName.setText(brandNameStr + ", " + quantity + " pcs");
            }

            // Set order ID
            String orderIDStr = extras.getString("order_id", "1324576809");
            if (orderID != null) {
                orderID.setText(orderIDStr);
            }

            // Set FIXED from address (always the same - business address)
            if (fromAddress != null) {
                fromAddress.setText("402 Bugayong Street Cabugao Molasagul Pangasinan Philippines");
            }

            // Set to address (customer's address - this can vary)
            String toAddressStr = extras.getString("to_address", "Block 2 Lot 17 Estrella Homes Phase 1 Toolong Kawit Cavite");
            if (toAddress != null) {
                toAddress.setText(toAddressStr);
            }

            // Set FIXED package weight (always 500 grams for pet food)
            if (packageWeight != null) {
                packageWeight.setText("500 grams");
            }

            // Set product image
            int imageResource = extras.getInt("product_image", 0);
            if (productImage != null && imageResource != 0) {
                productImage.setImageResource(imageResource);
            } else {
                // Set default image if no image is provided
                if (productImage != null) {
                    productImage.setImageResource(R.drawable.img_cooked_dog_food);
                }
            }

            // You can also get other data if needed:
            // String productPrice = extras.getString("product_price", "₱0.00");
            // String customerName = extras.getString("customer_name", "Customer");
            // String orderDate = extras.getString("order_date", "Date");
            // String paymentMethod = extras.getString("payment_method", "Payment Method");
        } else {
            // Set default values if no data is passed
            setDefaultValues();
        }
    }

    private void setDefaultValues() {
        if (productName != null) {
            productName.setText("Cooked Dog Food");
        }
        if (brandName != null) {
            brandName.setText("Chicken and White Rice, 1 pcs");
        }
        if (orderID != null) {
            orderID.setText("1324576809");
        }
        if (fromAddress != null) {
            fromAddress.setText("402 Bugayong Street Cabugao Molasagul Pangasinan Philippines");
        }
        if (toAddress != null) {
            toAddress.setText("Block 2 Lot 17 Estrella Homes Phase 1 Toolong Kawit Cavite");
        }
        if (packageWeight != null) {
            packageWeight.setText("500 grams");
        }
        if (productImage != null) {
            productImage.setImageResource(R.drawable.img_cooked_dog_food);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // Close this activity and return to previous screen
    }
}