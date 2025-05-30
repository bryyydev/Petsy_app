package com.ucucite.block_one_mob_dev;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Details extends AppCompatActivity {

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
            setupProductDetails();
            setupClickListeners();
        }
    }

    private void setupProductDetails() {
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
            android.util.Log.e("Details", "product_image ImageView not found in layout");
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
    }

    private void setupClickListeners() {
        // Back button
        ImageView backButton = findViewById(R.id.icon_back);
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        // Quantity buttons
        ImageButton decreaseButton = findViewById(R.id.btn_decrease_quantity);
        ImageButton increaseButton = findViewById(R.id.btn_increase_quantity);

        if (decreaseButton != null) {
            decreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (quantity > 1) {
                        quantity--;
                        updateQuantityAndPrice();
                    }
                }
            });
        }

        if (increaseButton != null) {
            increaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quantity++;
                    updateQuantityAndPrice();
                }
            });
        }

        // Add to cart button - UPDATED TO NAVIGATE TO CART
        Button addToCartButton = findViewById(R.id.btn_add_to_cart);
        if (addToCartButton != null) {
            addToCartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Add product to cart using CartManager
                    cartManager.addToCart(currentProduct, quantity);

                    // Show success message
                    Toast.makeText(Details.this,
                            currentProduct.getName() + " added to cart (Qty: " + quantity + ")",
                            Toast.LENGTH_SHORT).show();

                    // Navigate back to main activity and open cart fragment
                    navigateToCartFragment();
                }
            });
        }

        // Checkout button - UPDATED
        Button checkoutButton = findViewById(R.id.btn_checkout);
        if (checkoutButton != null) {
            checkoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Add current product to cart first
                    cartManager.addToCart(currentProduct, quantity);

                    Toast.makeText(Details.this,
                            "Item added to cart. Proceeding to checkout...",
                            Toast.LENGTH_SHORT).show();

                    // Navigate to cart fragment
                    navigateToCartFragment();
                }
            });
        }

        // Share button
        ImageView shareButton = findViewById(R.id.icon_share);
        if (shareButton != null) {
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Details.this, "Share functionality", Toast.LENGTH_SHORT).show();
                    // Implement share functionality here
                }
            });
        }

        // Favorite button
        ImageView favoriteButton = findViewById(R.id.icon_delete);
        if (favoriteButton != null) {
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Details.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                    // Implement favorite functionality here
                }
            });
        }
    }

    private void navigateToCartFragment() {
        // Create an intent to go back to MainActivity and open cart fragment
        Intent intent = new Intent();
        intent.putExtra("navigate_to_cart", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void updateQuantityAndPrice() {
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
            } catch (NumberFormatException e) {
                productPrice.setText(basePrice);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}