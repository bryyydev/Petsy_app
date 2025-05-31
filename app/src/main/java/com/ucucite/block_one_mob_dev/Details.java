package com.ucucite.block_one_mob_dev;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Details extends AppCompatActivity {

    private static final String TAG = "Details";
    private static final int CHECKOUT_REQUEST_CODE = 200;

    private Product currentProduct;
    private TextView quantityText;
    private int quantity = 1;
    private TextView productPrice;
    private String basePrice;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Initialize cart manager
        cartManager = CartManager.getInstance();

        // Get product from intent
        currentProduct = getIntent().getParcelableExtra("product");

        if (currentProduct != null) {
            Log.d(TAG, "Product loaded: " + currentProduct.getName());
            setupProductDetails();
            setupClickListeners();
        } else {
            Log.e(TAG, "No product data received");
            Toast.makeText(this, "Error: Product data not available", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupProductDetails() {
        try {
            // Find views
            ImageView productImage = findViewById(R.id.product_image);
            TextView productName = findViewById(R.id.product_name);
            productPrice = findViewById(R.id.product_price);
            TextView productIngredients = findViewById(R.id.product_ingredients);
            TextView brandName = findViewById(R.id.brand_name);
            TextView ratingText = findViewById(R.id.rating_text);
            quantityText = findViewById(R.id.quantity_text);

            // Set product information with null checks
            if (productImage != null) {
                productImage.setImageResource(currentProduct.getImageResource());
            } else {
                Log.w(TAG, "product_image ImageView not found in layout");
            }

            if (productName != null) {
                productName.setText(currentProduct.getName());
            }

            basePrice = currentProduct.getPrice();
            if (productPrice != null) {
                productPrice.setText(basePrice);
            }

            if (productIngredients != null) {
                productIngredients.setText(currentProduct.getIngredients());
            }

            if (brandName != null) {
                brandName.setText(currentProduct.getBrand());
            }

            if (ratingText != null) {
                ratingText.setText(currentProduct.getRating());
            }

            if (quantityText != null) {
                quantityText.setText(String.valueOf(quantity));
            }

            Log.d(TAG, "Product details setup completed");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up product details: " + e.getMessage(), e);
        }
    }

    private void setupClickListeners() {
        try {
            // Back button
            ImageView backButton = findViewById(R.id.icon_back);
            if (backButton != null) {
                backButton.setOnClickListener(v -> onBackPressed());
            }

            // Quantity buttons
            ImageButton decreaseButton = findViewById(R.id.btn_decrease_quantity);
            ImageButton increaseButton = findViewById(R.id.btn_increase_quantity);

            if (decreaseButton != null) {
                decreaseButton.setOnClickListener(v -> {
                    if (quantity > 1) {
                        quantity--;
                        updateQuantityAndPrice();
                        Log.d(TAG, "Quantity decreased to: " + quantity);
                    }
                });
            }

            if (increaseButton != null) {
                increaseButton.setOnClickListener(v -> {
                    quantity++;
                    updateQuantityAndPrice();
                    Log.d(TAG, "Quantity increased to: " + quantity);
                });
            }

            // Add to cart button
            Button addToCartButton = findViewById(R.id.btn_add_to_cart);
            if (addToCartButton != null) {
                addToCartButton.setOnClickListener(v -> addToCart());
            }

            // Checkout button
            Button checkoutButton = findViewById(R.id.btn_checkout);
            if (checkoutButton != null) {
                checkoutButton.setOnClickListener(v -> navigateToCheckout());
            }

            // Share button
            ImageView shareButton = findViewById(R.id.icon_share);
            if (shareButton != null) {
                shareButton.setOnClickListener(v -> shareProduct());
            }

            // Favorite button
            ImageView favoriteButton = findViewById(R.id.icon_delete);
            if (favoriteButton != null) {
                favoriteButton.setOnClickListener(v -> toggleFavorite());
            }

            Log.d(TAG, "Click listeners setup completed");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners: " + e.getMessage(), e);
        }
    }

    private void addToCart() {
        try {
            if (currentProduct == null) {
                Toast.makeText(this, "Product data not available", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add product to cart using CartManager
            cartManager.addToCart(currentProduct, quantity);

            // Show success message
            String message = currentProduct.getName() + " added to cart (Qty: " + quantity + ")";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Product added to cart: " + message);

            // Navigate back to main activity and open cart fragment
            navigateToCartFragment();
        } catch (Exception e) {
            Log.e(TAG, "Error adding to cart: " + e.getMessage(), e);
            Toast.makeText(this, "Error adding to cart", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToCheckout() {
        try {
            if (currentProduct == null) {
                Toast.makeText(this, "Product data not available", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent checkoutIntent = new Intent(Details.this, DetailsCheckout.class);
            checkoutIntent.putExtra("single_product", currentProduct);
            checkoutIntent.putExtra("product_quantity", quantity);
            checkoutIntent.putExtra("checkout_message", "Direct checkout for " + currentProduct.getName());

            Log.d(TAG, "Navigating to Checkout with product: " + currentProduct.getName() + ", quantity: " + quantity);
            startActivityForResult(checkoutIntent, CHECKOUT_REQUEST_CODE);

        } catch (Exception e) {
            Log.e(TAG, "Error navigating to checkout: " + e.getMessage(), e);
            Toast.makeText(this, "Error opening checkout", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToCartFragment() {
        try {
            // Create an intent to go back to MainActivity and open cart fragment
            Intent intent = new Intent();
            intent.putExtra("navigate_to_cart", true);
            setResult(RESULT_OK, intent);
            Log.d(TAG, "Navigating back to cart fragment");
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to cart fragment: " + e.getMessage(), e);
        }
    }

    private void shareProduct() {
        try {
            if (currentProduct == null) {
                Toast.makeText(this, "Product data not available", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareText = "Check out this product: " + currentProduct.getName() +
                    " by " + currentProduct.getBrand() +
                    " for " + currentProduct.getPrice();
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Product Recommendation");

            startActivity(Intent.createChooser(shareIntent, "Share Product"));
            Log.d(TAG, "Share intent created for product: " + currentProduct.getName());

        } catch (Exception e) {
            Log.e(TAG, "Error sharing product: " + e.getMessage(), e);
            Toast.makeText(this, "Error sharing product", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleFavorite() {
        try {
            if (currentProduct == null) {
                Toast.makeText(this, "Product data not available", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: Implement actual favorite functionality with database/preferences
            Toast.makeText(this, currentProduct.getName() + " added to favorites", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Product marked as favorite: " + currentProduct.getName());

            // You can implement actual favorite logic here:
            // - Save to SharedPreferences
            // - Save to database
            // - Update UI to show favorite state

        } catch (Exception e) {
            Log.e(TAG, "Error toggling favorite: " + e.getMessage(), e);
            Toast.makeText(this, "Error adding to favorites", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateQuantityAndPrice() {
        try {
            if (quantityText != null) {
                quantityText.setText(String.valueOf(quantity));
            }

            if (productPrice != null && basePrice != null) {
                // Extract numeric value from price string (assuming format like "₱170.00")
                String numericPrice = basePrice.replaceAll("[^\\d.]", "");
                try {
                    double price = Double.parseDouble(numericPrice);
                    double totalPrice = price * quantity;
                    productPrice.setText("₱" + String.format("%.2f", totalPrice));
                    Log.d(TAG, "Price updated: ₱" + String.format("%.2f", totalPrice) + " for quantity: " + quantity);
                } catch (NumberFormatException e) {
                    Log.w(TAG, "Could not parse price: " + basePrice, e);
                    productPrice.setText(basePrice);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating quantity and price: " + e.getMessage(), e);
        }
    }

    // Handle checkout result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == CHECKOUT_REQUEST_CODE && resultCode == RESULT_OK) {
                if (data != null && data.getBooleanExtra("checkout_success", false)) {
                    Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Checkout completed successfully");

                    // Navigate back to main activity
                    Intent intent = new Intent();
                    intent.putExtra("checkout_completed", true);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Log.d(TAG, "Checkout was cancelled or failed");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling activity result: " + e.getMessage(), e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            Log.d(TAG, "Back button pressed");
            super.onBackPressed();
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error handling back press: " + e.getMessage(), e);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Details activity destroyed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Details activity paused");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Details activity resumed");
    }
}