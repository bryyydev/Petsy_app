package com.ucucite.block_one_mob_dev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
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
    private static final String PREFS_NAME = "FavoritesPrefs";

    private Product currentProduct;
    private TextView quantityText;
    private int quantity = 1;
    private TextView productPrice;
    private String basePrice;
    private CartManager cartManager;
    private ImageView favoriteButton;
    private boolean isFavorite = false;
    private SharedPreferences favoritesPrefs;

    // User data variables
    private String userEmail;
    private String userStreetBarangay;
    private String userMunicipalityProvince;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Initialize cart manager and SharedPreferences
        cartManager = CartManager.getInstance();
        favoritesPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Get product and user data from intent
        currentProduct = getIntent().getParcelableExtra("product");

        // Retrieve user data from intent
        userEmail = getIntent().getStringExtra("user_email");
        userStreetBarangay = getIntent().getStringExtra("user_street_barangay");
        userMunicipalityProvince = getIntent().getStringExtra("user_municipality_province");

        Log.d(TAG, "User data received in Details - Email: " + userEmail +
                ", Street/Barangay: " + userStreetBarangay +
                ", Municipality/Province: " + userMunicipalityProvince);

        if (currentProduct != null) {
            Log.d(TAG, "Product loaded: " + currentProduct.getName());
            // Check if product is already in favorites
            checkFavoriteStatus();
            setupProductDetails();
            setupClickListeners();
        } else {
            Log.e(TAG, "No product data received");
            Toast.makeText(this, "Error: Product data not available", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void checkFavoriteStatus() {
        if (currentProduct != null) {
            String favoriteKey = "favorite_" + currentProduct.getName().replaceAll("\\s+", "_");
            isFavorite = favoritesPrefs.getBoolean(favoriteKey, false);
            Log.d(TAG, "Product favorite status: " + isFavorite);
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
            favoriteButton = findViewById(R.id.icon_favorite);

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

            // Set initial favorite button color
            updateFavoriteButtonColor();

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

            // Pass user data to DetailsCheckout
            checkoutIntent.putExtra("user_email", userEmail);
            checkoutIntent.putExtra("user_street_barangay", userStreetBarangay);
            checkoutIntent.putExtra("user_municipality_province", userMunicipalityProvince);

            Log.d(TAG, "Navigating to Checkout with product: " + currentProduct.getName() + ", quantity: " + quantity);
            Log.d(TAG, "Passing user data to Checkout - Email: " + userEmail +
                    ", Street/Barangay: " + userStreetBarangay +
                    ", Municipality/Province: " + userMunicipalityProvince);

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

            // Toggle favorite status
            isFavorite = !isFavorite;

            // Save favorite status to SharedPreferences
            String favoriteKey = "favorite_" + currentProduct.getName().replaceAll("\\s+", "_");
            SharedPreferences.Editor editor = favoritesPrefs.edit();
            editor.putBoolean(favoriteKey, isFavorite);
            editor.apply();

            // Update the heart icon color
            updateFavoriteButtonColor();

            // Show appropriate message
            String message = isFavorite ?
                    currentProduct.getName() + " added to favorites" :
                    currentProduct.getName() + " removed from favorites";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            Log.d(TAG, "Product favorite status changed: " + currentProduct.getName() + " - " + isFavorite);

        } catch (Exception e) {
            Log.e(TAG, "Error toggling favorite: " + e.getMessage(), e);
            Toast.makeText(this, "Error updating favorites", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFavoriteButtonColor() {
        try {
            if (favoriteButton != null) {
                if (isFavorite) {
                    // Set heart to solid red when favorited
                    favoriteButton.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
                } else {
                    // Clear color filter for default appearance when not favorited
                    favoriteButton.clearColorFilter();
                }
                Log.d(TAG, "Favorite button color updated. isFavorite: " + isFavorite);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating favorite button color: " + e.getMessage(), e);
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