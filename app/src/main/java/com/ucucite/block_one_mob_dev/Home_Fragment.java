package com.ucucite.block_one_mob_dev;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class Home_Fragment extends Fragment {

    private static final String TAG = "Home_Fragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private List<Product> products;

    // User data variables
    private String userEmail;
    private String userStreetBarangay;
    private String userMunicipalityProvince;

    public Home_Fragment() {
        // Required empty public constructor
    }

    public static Home_Fragment newInstance(String param1, String param2) {
        Home_Fragment fragment = new Home_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            // Retrieve user data from arguments
            userEmail = getArguments().getString("EMAIL");
            userStreetBarangay = getArguments().getString("STREET_BARANGAY");
            userMunicipalityProvince = getArguments().getString("MUNICIPALITY_PROVINCE");

            // Debug: Log all fragment arguments
            Log.d(TAG, "All fragment arguments:");
            for (String key : getArguments().keySet()) {
                Object value = getArguments().get(key);
                Log.d(TAG, key + " = " + (value != null ? value.toString() : "null"));
            }

            // If address data is not passed through arguments, fetch from database
            if ((userStreetBarangay == null || userStreetBarangay.isEmpty() ||
                    userMunicipalityProvince == null || userMunicipalityProvince.isEmpty())
                    && userEmail != null && !userEmail.isEmpty()) {

                Log.d(TAG, "Address data not found in arguments, fetching from database...");
                fetchAddressFromDatabase();
            }

            Log.d(TAG, "User data received - Email: " + userEmail +
                    ", Street/Barangay: " + userStreetBarangay +
                    ", Municipality/Province: " + userMunicipalityProvince);
        }

        try {
            // Initialize products data
            products = ProductData.getProducts();
            Log.d(TAG, "Products loaded: " + products.size());
        } catch (Exception e) {
            Log.e(TAG, "Error loading products: " + e.getMessage(), e);
            products = new java.util.ArrayList<>(); // Empty list as fallback
        }
    }

    private void fetchAddressFromDatabase() {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            DatabaseHelper.UserInfo userInfo = dbHelper.getUserByEmail(userEmail);

            if (userInfo != null) {
                // Build street/barangay string
                StringBuilder streetBarangay = new StringBuilder();
                if (userInfo.houseStreet != null && !userInfo.houseStreet.trim().isEmpty()) {
                    streetBarangay.append(userInfo.houseStreet);
                }
                if (userInfo.barangay != null && !userInfo.barangay.trim().isEmpty()) {
                    if (streetBarangay.length() > 0) {
                        streetBarangay.append(", ");
                    }
                    streetBarangay.append(userInfo.barangay);
                }

                // Build municipality/province string
                StringBuilder municipalityProvince = new StringBuilder();
                if (userInfo.town != null && !userInfo.town.trim().isEmpty()) {
                    municipalityProvince.append(userInfo.town);
                }
                if (userInfo.province != null && !userInfo.province.trim().isEmpty()) {
                    if (municipalityProvince.length() > 0) {
                        municipalityProvince.append(", ");
                    }
                    municipalityProvince.append(userInfo.province);
                }

                // Set the address data
                userStreetBarangay = streetBarangay.toString();
                userMunicipalityProvince = municipalityProvince.toString();

                Log.d(TAG, "Address fetched from database - Street/Barangay: " + userStreetBarangay +
                        ", Municipality/Province: " + userMunicipalityProvince);
            } else {
                Log.w(TAG, "No user found in database for email: " + userEmail);
                userStreetBarangay = "";
                userMunicipalityProvince = "";
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching user address from database: " + e.getMessage(), e);
            userStreetBarangay = "";
            userMunicipalityProvince = "";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        try {
            // Setup user address display
            setupUserAddressDisplay(view);

            // Setup click listeners for all product boxes
            setupProductClickListeners(view);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up home fragment: " + e.getMessage(), e);
            if (getContext() != null) {
                Toast.makeText(getContext(), "Error loading home screen", Toast.LENGTH_SHORT).show();
            }
        }

        return view;
    }

    private void setupUserAddressDisplay(View view) {
        try {
            // Find the TextViews for address display
            TextView streetBarangayTextView = view.findViewById(R.id.street_barangay);
            TextView municipalityProvinceTextView = view.findViewById(R.id.municipality_province);

            // Set the address text if TextViews exist and user data is available
            if (streetBarangayTextView != null) {
                if (userStreetBarangay != null && !userStreetBarangay.trim().isEmpty()) {
                    streetBarangayTextView.setText(userStreetBarangay);
                    Log.d(TAG, "Street/Barangay set: " + userStreetBarangay);
                } else {
                    streetBarangayTextView.setText("Address not available");
                    Log.w(TAG, "Street/Barangay data is empty or null");
                }
            } else {
                Log.w(TAG, "Street/Barangay TextView not found in layout");
            }

            if (municipalityProvinceTextView != null) {
                if (userMunicipalityProvince != null && !userMunicipalityProvince.trim().isEmpty()) {
                    municipalityProvinceTextView.setText(userMunicipalityProvince);
                    Log.d(TAG, "Municipality/Province set: " + userMunicipalityProvince);
                } else {
                    municipalityProvinceTextView.setText("Location not available");
                    Log.w(TAG, "Municipality/Province data is empty or null");
                }
            } else {
                Log.w(TAG, "Municipality/Province TextView not found in layout");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up user address display: " + e.getMessage(), e);
        }
    }

    private void setupProductClickListeners(View view) {
        // Array of product box IDs
        int[] productBoxIds = {
                R.id.product_box_1, R.id.product_box_2, R.id.product_box_3,
                R.id.product_box_4, R.id.product_box_5, R.id.product_box_6,
                R.id.product_box_7, R.id.product_box_8, R.id.product_box_9,
                R.id.product_box_10, R.id.product_box_11, R.id.product_box_12,
                R.id.product_box_13, R.id.product_box_14, R.id.product_box_15,
                R.id.product_box_16, R.id.product_box_17, R.id.product_box_18
        };

        for (int i = 0; i < productBoxIds.length && i < products.size(); i++) {
            try {
                LinearLayout productBox = view.findViewById(productBoxIds[i]);
                if (productBox != null) {
                    final int productIndex = i;
                    final Product product = products.get(i);

                    // Update product information in the layout
                    updateProductUI(productBox, product);

                    // Set click listener
                    productBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Log.d(TAG, "Product clicked: " + product.getName());
                                openProductDetails(product);
                            } catch (Exception e) {
                                Log.e(TAG, "Error opening product details: " + e.getMessage(), e);
                                if (getContext() != null) {
                                    Toast.makeText(getContext(), "Error opening product details", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                } else {
                    Log.w(TAG, "Product box not found for ID: " + productBoxIds[i]);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error setting up product box " + i + ": " + e.getMessage(), e);
            }
        }
    }

    private void updateProductUI(LinearLayout productBox, Product product) {
        try {
            // Update product image
            ImageView productImage = productBox.findViewById(getProductImageId(product.getId()));
            if (productImage != null) {
                productImage.setImageResource(product.getImageResource());
            } else {
                Log.w(TAG, "Product image not found for product ID: " + product.getId());
            }

            // Update product name
            TextView productName = productBox.findViewById(getProductNameId(product.getId()));
            if (productName != null) {
                productName.setText(product.getName());
            } else {
                Log.w(TAG, "Product name TextView not found for product ID: " + product.getId());
            }

            // Update product price
            TextView productPrice = productBox.findViewById(getProductPriceId(product.getId()));
            if (productPrice != null) {
                productPrice.setText(product.getPrice());
            } else {
                Log.w(TAG, "Product price TextView not found for product ID: " + product.getId());
            }

            // Update rating if available
            TextView ratingText = productBox.findViewById(getRatingTextId(product.getId()));
            if (ratingText != null) {
                ratingText.setText(product.getRating());
            } else {
                Log.w(TAG, "Rating TextView not found for product ID: " + product.getId());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating product UI for product: " + product.getName() + " - " + e.getMessage(), e);
        }
    }

    private int getProductImageId(int productId) {
        switch (productId) {
            case 1: return R.id.product_image_1;
            case 2: return R.id.product_image_2;
            case 3: return R.id.product_image_3;
            case 4: return R.id.product_image_4;
            case 5: return R.id.product_image_5;
            case 6: return R.id.product_image_6;
            case 7: return R.id.product_image_7;
            case 8: return R.id.product_image_8;
            case 9: return R.id.product_image_9;
            case 10: return R.id.product_image_10;
            case 11: return R.id.product_image_11;
            case 12: return R.id.product_image_12;
            case 13: return R.id.product_image_13;
            case 14: return R.id.product_image_14;
            case 15: return R.id.product_image_15;
            case 16: return R.id.product_image_16;
            case 17: return R.id.product_image_17;
            case 18: return R.id.product_image_18;
            default: return R.id.product_image_1;
        }
    }

    private int getProductNameId(int productId) {
        switch (productId) {
            case 1: return R.id.product_name_1;
            case 2: return R.id.product_name_2;
            case 3: return R.id.product_name_3;
            case 4: return R.id.product_name_4;
            case 5: return R.id.product_name_5;
            case 6: return R.id.product_name_6;
            case 7: return R.id.product_name_7;
            case 8: return R.id.product_name_8;
            case 9: return R.id.product_name_9;
            case 10: return R.id.product_name_10;
            case 11: return R.id.product_name_11;
            case 12: return R.id.product_name_12;
            case 13: return R.id.product_name_13;
            case 14: return R.id.product_name_14;
            case 15: return R.id.product_name_15;
            case 16: return R.id.product_name_16;
            case 17: return R.id.product_name_17;
            case 18: return R.id.product_name_18;
            default: return R.id.product_name_1;
        }
    }

    private int getRatingTextId(int productId) {
        switch (productId) {
            case 1: return R.id.rating_text_1;
            case 2: return R.id.rating_text_2;
            case 3: return R.id.rating_text_3;
            case 4: return R.id.rating_text_4;
            case 5: return R.id.rating_text_5;
            case 6: return R.id.rating_text_6;
            case 7: return R.id.rating_text_7;
            case 8: return R.id.rating_text_8;
            case 9: return R.id.rating_text_9;
            case 10: return R.id.rating_text_10;
            case 11: return R.id.rating_text_11;
            case 12: return R.id.rating_text_12;
            case 13: return R.id.rating_text_13;
            case 14: return R.id.rating_text_14;
            case 15: return R.id.rating_text_15;
            case 16: return R.id.rating_text_16;
            case 17: return R.id.rating_text_17;
            case 18: return R.id.rating_text_18;
            default: return R.id.rating_text_1;
        }
    }

    private int getProductPriceId(int productId) {
        switch (productId) {
            case 1: return R.id.product_price_1;
            case 2: return R.id.product_price_2;
            case 3: return R.id.product_price_3;
            case 4: return R.id.product_price_4;
            case 5: return R.id.product_price_5;
            case 6: return R.id.product_price_6;
            case 7: return R.id.product_price_7;
            case 8: return R.id.product_price_8;
            case 9: return R.id.product_price_9;
            case 10: return R.id.product_price_10;
            case 11: return R.id.product_price_11;
            case 12: return R.id.product_price_12;
            case 13: return R.id.product_price_13;
            case 14: return R.id.product_price_14;
            case 15: return R.id.product_price_15;
            case 16: return R.id.product_price_16;
            case 17: return R.id.product_price_17;
            case 18: return R.id.product_price_18;
            default: return R.id.product_price_1;
        }
    }

    // In Home_Fragment.java - modify the openProductDetails method

    private void openProductDetails(Product product) {
        try {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), Details.class);
                intent.putExtra("product", product);

                // Pass user data to Details activity
                intent.putExtra("user_email", userEmail);
                intent.putExtra("user_street_barangay", userStreetBarangay);
                intent.putExtra("user_municipality_province", userMunicipalityProvince);

                Log.d(TAG, "Starting Details activity for: " + product.getName());
                Log.d(TAG, "Passing user data - Email: " + userEmail +
                        ", Street/Barangay: " + userStreetBarangay +
                        ", Municipality/Province: " + userMunicipalityProvince);
                startActivity(intent);
            } else {
                Log.e(TAG, "Activity is null, cannot start Details activity");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error starting Details activity: " + e.getMessage(), e);
            if (getContext() != null) {
                Toast.makeText(getContext(), "Error opening product details", Toast.LENGTH_SHORT).show();
            }
        }
    }}