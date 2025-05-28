package com.ucucite.block_one_mob_dev;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

public class Edit_Address extends AppCompatActivity {

    private ImageView arrowBack, editIcon;
    private TextView textViewFullName, textViewPhone;
    private Spinner spinnerProvince, spinnerTown, spinnerBarangay;
    private EditText editTextHouse;
    private Button saveAddressBtn;

    // Database helper
    private DatabaseHelper dbHelper;

    // User data
    private String userEmail;
    private DatabaseHelper.UserInfo currentUser;

    // Maps for dropdown data
    private final Map<String, List<String>> provinceTownMap = new HashMap<>();
    private final Map<String, List<String>> townBarangayMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);

        try {
            // Initialize database helper
            dbHelper = new DatabaseHelper(this);

            // Get user email from intent or shared preferences
            getUserEmail();

            // Initialize views
            initViews();

            // Load user data and populate fields
            loadUserData();

            // Setup dropdown data and functionality
            setupDropdownMaps();
            setupDropdowns();

            // Setup click listeners
            setupClickListeners();

            Log.d("EditAddress", "Edit_Address activity started for user: " + userEmail);
        } catch (Exception e) {
            Log.e("EditAddress", "Error in onCreate: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error loading page: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void getUserEmail() {
        // Get email from intent first - FIXED: Changed from "USER_EMAIL" to "user_email"
        userEmail = getIntent().getStringExtra("user_email");

        // If not in intent, try to get from SharedPreferences
        if (userEmail == null || userEmail.isEmpty()) {
            userEmail = getSharedPreferences("UserSession", MODE_PRIVATE)
                    .getString("user_email", "");
        }

        Log.d("EditAddress", "User email: " + userEmail);
    }

    private void loadUserData() {
        if (userEmail == null || userEmail.isEmpty()) {
            Log.e("EditAddress", "No user email available");
            Toast.makeText(this, "Error: User session not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            // Get user data from database
            currentUser = dbHelper.getUserByEmail(userEmail);

            if (currentUser != null) {
                // Display full name
                String fullName = currentUser.getFullName();
                if (fullName.isEmpty() && currentUser.username != null) {
                    fullName = currentUser.username; // Fallback to username if no first/last name
                }
                textViewFullName.setText(fullName);

                // Display phone number
                String phoneNumber = currentUser.phoneNumber != null ? currentUser.phoneNumber : "No phone number";
                textViewPhone.setText(phoneNumber);

                // Set current house/street address
                if (currentUser.houseStreet != null && !currentUser.houseStreet.trim().isEmpty()) {
                    editTextHouse.setText(currentUser.houseStreet);
                }

                Log.d("EditAddress", "User data loaded - Name: " + fullName + ", Phone: " + phoneNumber);
            } else {
                Log.e("EditAddress", "User not found in database");
                Toast.makeText(this, "Error: User data not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Log.e("EditAddress", "Error loading user data: " + e.getMessage());
            Toast.makeText(this, "Error loading user data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        try {
            arrowBack = findViewById(R.id.arrow_back);
            editIcon = findViewById(R.id.editIcon);
            textViewFullName = findViewById(R.id.textViewFullName);
            textViewPhone = findViewById(R.id.textViewPhone);
            spinnerProvince = findViewById(R.id.spinnerProvince);
            spinnerTown = findViewById(R.id.spinnerTown);
            spinnerBarangay = findViewById(R.id.spinnerBarangay);
            editTextHouse = findViewById(R.id.editTextHouse);
            saveAddressBtn = findViewById(R.id.saveAddressBtn);

            // Check if any view is null
            if (arrowBack == null || editIcon == null || textViewFullName == null ||
                    textViewPhone == null || spinnerProvince == null || spinnerTown == null ||
                    spinnerBarangay == null || editTextHouse == null || saveAddressBtn == null) {
                throw new RuntimeException("One or more views not found in layout");
            }
        } catch (Exception e) {
            Log.e("EditAddress", "Error initializing views: " + e.getMessage());
            throw e;
        }
    }

    private void setupDropdownMaps() {
        // Province and Town mapping
        provinceTownMap.put("Ilocos Norte", Arrays.asList("Laoag City", "Batac", "Paoay"));
        provinceTownMap.put("Ilocos Sur", Arrays.asList("Vigan City", "Candon", "Narvacan"));
        provinceTownMap.put("La Union", Arrays.asList("San Fernando City", "Agoo", "Bauang"));
        provinceTownMap.put("Pangasinan", Arrays.asList("Dagupan City", "Urdaneta", "Alaminos"));

        // Town and Barangay mapping
        townBarangayMap.put("Laoag City", Arrays.asList("San Lorenzo", "San Joaquina", "Nuestra Senora", "San Guillermo", "San Pedro"));
        townBarangayMap.put("Batac", Arrays.asList("Ablan", "Acosta", "Baay", "Baligat", "Billoca"));
        townBarangayMap.put("Paoay", Arrays.asList("Bacsil", "Cabagoan", "Cabangaran", "Calleguip", "Cayubog"));
        townBarangayMap.put("Vigan City", Arrays.asList("Ayusan Norte", "Ayusan Sur"));
        townBarangayMap.put("Candon", Arrays.asList("Campo", "Gabor", "Tocgo"));
        townBarangayMap.put("Narvacan", Arrays.asList("Bulanos", "Cadacad"));
        townBarangayMap.put("San Fernando City", Arrays.asList("Bacsil", "Bangcusay"));
        townBarangayMap.put("Agoo", Arrays.asList("Ambalita", "Aringay"));
        townBarangayMap.put("Bauang", Arrays.asList("Bacuit Norte", "Baccuit Sur"));
        townBarangayMap.put("Dagupan City", Arrays.asList("Cabaroan", "Camalio", "Bonuan Gueset"));
        townBarangayMap.put("Urdaneta", Arrays.asList("Bactad East", "Bayaoas", "Camnantiles", "Casantaan"));
        townBarangayMap.put("Alaminos", Arrays.asList("Bisocol", "Cayucay"));
    }

    private void setupDropdowns() {
        try {
            // Setup Province Spinner with user's current province or default
            String currentProvince = (currentUser != null && currentUser.province != null) ? currentUser.province : "Choose your Province";
            List<String> provinceList = new ArrayList<>();
            if (!currentProvince.equals("Choose your Province")) {
                provinceList.add(currentProvince);
            } else {
                provinceList.add("Choose your Province");
            }

            // Add all other provinces
            for (String province : provinceTownMap.keySet()) {
                if (!province.equals(currentProvince)) {
                    provinceList.add(province);
                }
            }
            spinnerProvince.setAdapter(getSimpleAdapter(provinceList));

            // Setup Town Spinner with user's current town or default
            String currentTown = (currentUser != null && currentUser.town != null) ? currentUser.town : "Choose your Town";
            List<String> townList = new ArrayList<>();

            if (!currentTown.equals("Choose your Town") && currentUser != null && currentUser.province != null) {
                List<String> availableTowns = provinceTownMap.get(currentUser.province);
                if (availableTowns != null && availableTowns.contains(currentTown)) {
                    townList.add(currentTown);
                    // Add other towns from the same province
                    for (String town : availableTowns) {
                        if (!town.equals(currentTown)) {
                            townList.add(town);
                        }
                    }
                } else {
                    townList.add("Choose your Town");
                }
            } else {
                townList.add("Choose your Town");
            }
            spinnerTown.setAdapter(getSimpleAdapter(townList));

            // Setup Barangay Spinner with user's current barangay or default
            String currentBarangay = (currentUser != null && currentUser.barangay != null) ? currentUser.barangay : "Choose your Barangay";
            List<String> barangayList = new ArrayList<>();

            if (!currentBarangay.equals("Choose your Barangay") && currentUser != null && currentUser.town != null) {
                List<String> availableBarangays = townBarangayMap.get(currentUser.town);
                if (availableBarangays != null && availableBarangays.contains(currentBarangay)) {
                    barangayList.add(currentBarangay);
                    // Add other barangays from the same town
                    for (String barangay : availableBarangays) {
                        if (!barangay.equals(currentBarangay)) {
                            barangayList.add(barangay);
                        }
                    }
                } else {
                    barangayList.add("Choose your Barangay");
                }
            } else {
                barangayList.add("Choose your Barangay");
            }
            spinnerBarangay.setAdapter(getSimpleAdapter(barangayList));

            // Province selection listener
            spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                    String selectedProvince = (String) spinnerProvince.getSelectedItem();
                    if (!selectedProvince.equals("Choose your Province")) {
                        List<String> towns = provinceTownMap.get(selectedProvince);
                        if (towns != null) {
                            List<String> updatedTownList = new ArrayList<>();
                            // If user has a town in this province, show it first
                            if (currentUser != null && currentUser.town != null && towns.contains(currentUser.town) && selectedProvince.equals(currentUser.province)) {
                                updatedTownList.add(currentUser.town);
                                for (String town : towns) {
                                    if (!town.equals(currentUser.town)) {
                                        updatedTownList.add(town);
                                    }
                                }
                            } else {
                                updatedTownList.add("Choose your Town");
                                updatedTownList.addAll(towns);
                            }
                            spinnerTown.setAdapter(getSimpleAdapter(updatedTownList));

                            // Reset barangay spinner
                            spinnerBarangay.setAdapter(getSimpleAdapter(Collections.singletonList("Choose your Barangay")));
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {}
            });

            // Town selection listener
            spinnerTown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                    String selectedTown = (String) spinnerTown.getSelectedItem();
                    if (!selectedTown.equals("Choose your Town")) {
                        List<String> barangays = townBarangayMap.get(selectedTown);
                        if (barangays != null) {
                            List<String> updatedBarangayList = new ArrayList<>();
                            // If user has a barangay in this town, show it first
                            if (currentUser != null && currentUser.barangay != null && barangays.contains(currentUser.barangay) && selectedTown.equals(currentUser.town)) {
                                updatedBarangayList.add(currentUser.barangay);
                                for (String barangay : barangays) {
                                    if (!barangay.equals(currentUser.barangay)) {
                                        updatedBarangayList.add(barangay);
                                    }
                                }
                            } else {
                                updatedBarangayList.add("Choose your Barangay");
                                updatedBarangayList.addAll(barangays);
                            }
                            spinnerBarangay.setAdapter(getSimpleAdapter(updatedBarangayList));
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {}
            });

        } catch (Exception e) {
            Log.e("EditAddress", "Error setting up dropdowns: " + e.getMessage());
            Toast.makeText(this, "Error setting up dropdowns", Toast.LENGTH_SHORT).show();
        }
    }

    // FIXED: Simplified adapter to avoid layout issues
    private ArrayAdapter<String> getSimpleAdapter(List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private void setupClickListeners() {
        // Back arrow click listener
        arrowBack.setOnClickListener(v -> {
            Log.d("EditAddress", "Back button clicked");
            finish();
        });

        // Edit icon click listener
        editIcon.setOnClickListener(v -> {
            Log.d("EditAddress", "Edit icon clicked");
            Toast.makeText(this, "Edit address functionality", Toast.LENGTH_SHORT).show();
        });

        // Save address button click listener
        saveAddressBtn.setOnClickListener(v -> {
            saveAddress();
        });
    }

    private void saveAddress() {
        try {
            // Get all input values
            String province = spinnerProvince.getSelectedItem().toString().trim();
            String town = spinnerTown.getSelectedItem().toString().trim();
            String barangay = spinnerBarangay.getSelectedItem().toString().trim();
            String houseStreet = editTextHouse.getText().toString().trim();

            Log.d("EditAddress", "Save address clicked");
            Log.d("EditAddress", "Province: " + province);
            Log.d("EditAddress", "Town: " + town);
            Log.d("EditAddress", "Barangay: " + barangay);
            Log.d("EditAddress", "House/Street: " + houseStreet);

            // Validate inputs
            if (!validateAddressInputs(province, town, barangay, houseStreet)) {
                return;
            }

            // Update address in database
            boolean success = dbHelper.updateUserAddress(userEmail, province, town, barangay, houseStreet);

            if (success) {
                Toast.makeText(this, "Address updated successfully!", Toast.LENGTH_SHORT).show();
                Log.d("EditAddress", "Address updated successfully for user: " + userEmail);

                // Set result to indicate successful update
                Intent resultIntent = new Intent();
                resultIntent.putExtra("ADDRESS_UPDATED", true);
                setResult(RESULT_OK, resultIntent);

                // Close the activity
                finish();
            } else {
                Toast.makeText(this, "Failed to update address. Please try again.", Toast.LENGTH_SHORT).show();
                Log.e("EditAddress", "Failed to update address for user: " + userEmail);
            }
        } catch (Exception e) {
            Log.e("EditAddress", "Error saving address: " + e.getMessage());
            Toast.makeText(this, "Error saving address: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateAddressInputs(String province, String town, String barangay, String houseStreet) {
        // Check if required fields are empty
        if (houseStreet.isEmpty()) {
            Toast.makeText(this, "Please fill in house number and street", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if dropdowns are selected properly
        if (province.equals("Choose your Province") ||
                town.equals("Choose your Town") ||
                barangay.equals("Choose your Barangay")) {
            Toast.makeText(this, "Please select Province, Town, and Barangay", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up database helper
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}