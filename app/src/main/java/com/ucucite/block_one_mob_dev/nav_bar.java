package com.ucucite.block_one_mob_dev;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class nav_bar extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private String loggedInEmail;  // store logged-in email here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_bar);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Receive email passed from Login activity
        loggedInEmail = getIntent().getStringExtra("EMAIL");

        // Load the default fragment (Home_Fragment)
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new Home_Fragment())
                    .commit();
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
}
