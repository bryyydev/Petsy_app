package com.ucucite.block_one_mob_dev;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class nav_bar extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private String loggedInEmail; // store logged-in email here
    private ActivityResultLauncher<Intent> detailsActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_bar);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Receive email passed from Login activity
        loggedInEmail = getIntent().getStringExtra("EMAIL");

        // Initialize the activity result launcher for Details activity
        detailsActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        boolean navigateToCart = result.getData().getBooleanExtra("navigate_to_cart", false);
                        if (navigateToCart) {
                            navigateToCartFragment();
                        }
                    }
                }
        );

        // Check if we need to navigate to cart
        boolean navigateToCart = getIntent().getBooleanExtra("navigate_to_cart", false);

        // Load the appropriate fragment
        if (savedInstanceState == null) {
            if (navigateToCart) {
                // Navigate to cart fragment
                navigateToCartFragment();
            } else {
                // Load default fragment (Home_Fragment)
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new Home_Fragment())
                        .commit();
            }
        }

        bottomNavigationView.setItemIconTintList(null);

        // Handle navigation item clicks
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new Home_Fragment();
            } else if (itemId == R.id.nav_cart) {
                selectedFragment = new Cart_Fragment();
            } else if (itemId == R.id.nav_orders) {
                selectedFragment = new Orders_Fragment();
            } else if (itemId == R.id.nav_profile) {
                // Pass email to Profile_Fragment
                Profile_Fragment profileFragment = new Profile_Fragment();
                Bundle bundle = new Bundle();
                bundle.putString("EMAIL", loggedInEmail);
                profileFragment.setArguments(bundle);
                selectedFragment = profileFragment;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }

    // Method to launch Details activity - call this from your fragments
    public void launchDetailsActivity(Product product) {
        Intent intent = new Intent(this, Details.class);
        intent.putExtra("product", product);
        detailsActivityLauncher.launch(intent);
    }

    // Method to navigate to cart fragment
    private void navigateToCartFragment() {
        // Set the bottom navigation to cart item
        bottomNavigationView.setSelectedItemId(R.id.nav_cart);

        // Replace the current fragment with Cart_Fragment
        Cart_Fragment cartFragment = new Cart_Fragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, cartFragment)
                .commit();
    }

    // Public method to get the activity result launcher (optional, for fragments to use)
    public ActivityResultLauncher<Intent> getDetailsActivityLauncher() {
        return detailsActivityLauncher;
    }
}