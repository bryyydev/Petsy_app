package com.ucucite.block_one_mob_dev;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.IOException;
import java.util.*;

public class Edit_Info extends AppCompatActivity {

    private ShapeableImageView profileImageView;
    private ImageView editIcon;
    private EditText firstName, lastName, phoneNumber, houseStreet;
    private Spinner spinnerProvince, spinnerTown, spinnerBarangay;
    private Button createAccountBtn;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private final Map<String, List<String>> provinceTownMap = new HashMap<>();
    private final Map<String, List<String>> townBarangayMap = new HashMap<>();

    private DatabaseHelper dbHelper; // FIXED: Use instance variable properly
    private String userEmail;
    private String userUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        // FIXED: Initialize instance variable instead of local variable
        dbHelper = new DatabaseHelper(this);
        dbHelper.logAllUsers();

        // Get user information from intent
        getUserDataFromIntent();

        // Initialize views and setup functionality
        initViews();
        setupDropdownMaps();
        setupDropdowns();
        setupImagePicker();
        handleFormSubmission();

        // Debug log
        Log.d("EditInfo", "Edit_Info activity started");
        Log.d("EditInfo", "User email: " + userEmail);
        Log.d("EditInfo", "Username: " + userUsername);
        dbHelper.logAllUsers();
    }

    private void getUserDataFromIntent() {
        Intent intent = getIntent();
        userEmail = intent.getStringExtra("user_email");
        userUsername = intent.getStringExtra("username");

        Log.d("EditInfo", "Received email: " + userEmail);
        Log.d("EditInfo", "Received username: " + userUsername);

        // Fallback if email is not passed
        if (userEmail == null || userEmail.isEmpty()) {
            Log.e("EditInfo", "No email received from Sign_up activity!");
            Toast.makeText(this, "Error: No user data received. Please sign up again.", Toast.LENGTH_LONG).show();
            // Navigate back to sign up
            Intent signUpIntent = new Intent(Edit_Info.this, Sign_up.class);
            startActivity(signUpIntent);
            finish();
            return;
        }
    }

    private void initViews() {
        profileImageView = findViewById(R.id.imageView);
        editIcon = findViewById(R.id.edit_icon);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        phoneNumber = findViewById(R.id.phoneNumber);
        houseStreet = findViewById(R.id.houseStreet);

        spinnerProvince = findViewById(R.id.spinnerProvince);
        spinnerTown = findViewById(R.id.spinnerTown);
        spinnerBarangay = findViewById(R.id.spinnerBarangay);

        createAccountBtn = findViewById(R.id.createAccountBtn);

        // Set up image picker
        if (editIcon != null) {
            editIcon.setOnClickListener(v -> pickImageFromGallery());
        } else {
            Log.w("EditInfo", "Edit icon not found in layout");
        }
    }

    private void setupDropdownMaps() {
        // Province and Town mapping
        provinceTownMap.put("Ilocos Norte", Arrays.asList("Laoag City", "Batac", "Paoay"));
        provinceTownMap.put("Ilocos Sur", Arrays.asList("Vigan City", "Candon", "Narvacan"));
        provinceTownMap.put("La Union", Arrays.asList("San Fernando City", "Agoo", "Bauang"));
        provinceTownMap.put("Pangasinan", Arrays.asList("Dagupan City", "Urdaneta", "Alaminos"));

        // Town and Barangay mapping
        townBarangayMap.put("Laoag City", Arrays.asList("Barangay 1", "Barangay 2", "Barangay 3"));
        townBarangayMap.put("Batac", Arrays.asList("Barangay A", "Barangay B", "Barangay C"));
        townBarangayMap.put("Paoay", Arrays.asList("Barangay X", "Barangay Y"));
        townBarangayMap.put("Vigan City", Arrays.asList("Barangay I", "Barangay II", "Barangay III"));
        townBarangayMap.put("Candon", Arrays.asList("Barangay Alpha", "Barangay Beta"));
        townBarangayMap.put("Narvacan", Arrays.asList("Barangay Gamma", "Barangay Delta"));
        townBarangayMap.put("San Fernando City", Arrays.asList("Barangay Norte", "Barangay Sur"));
        townBarangayMap.put("Agoo", Arrays.asList("Barangay East", "Barangay West"));
        townBarangayMap.put("Bauang", Arrays.asList("Barangay Centro", "Barangay Poblacion"));
        townBarangayMap.put("Dagupan City", Arrays.asList("Lucao", "Pantal", "Bonuan Gueset"));
        townBarangayMap.put("Urdaneta", Arrays.asList("Barangay 1", "Barangay 2"));
        townBarangayMap.put("Alaminos", Arrays.asList("Barangay Poblacion", "Barangay Tanaytay"));
    }

    private void setupDropdowns() {
        // Setup Province Spinner
        List<String> provinceList = new ArrayList<>();
        provinceList.add("Choose your Province");
        provinceList.addAll(provinceTownMap.keySet());
        spinnerProvince.setAdapter(getCustomAdapter(provinceList));

        // Initialize other spinners with default values
        spinnerTown.setAdapter(getCustomAdapter(Collections.singletonList("Choose your Town")));
        spinnerBarangay.setAdapter(getCustomAdapter(Collections.singletonList("Choose your Barangay")));

        // Province selection listener
        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                String selectedProvince = (String) spinnerProvince.getSelectedItem();
                if (!selectedProvince.equals("Choose your Province")) {
                    List<String> towns = provinceTownMap.get(selectedProvince);
                    if (towns != null) {
                        List<String> updatedTownList = new ArrayList<>();
                        updatedTownList.add("Choose your Town");
                        updatedTownList.addAll(towns);
                        spinnerTown.setAdapter(getCustomAdapter(updatedTownList));

                        // Reset barangay spinner
                        spinnerBarangay.setAdapter(getCustomAdapter(Collections.singletonList("Choose your Barangay")));
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
                        updatedBarangayList.add("Choose your Barangay");
                        updatedBarangayList.addAll(barangays);
                        spinnerBarangay.setAdapter(getCustomAdapter(updatedBarangayList));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    private ArrayAdapter<String> getCustomAdapter(List<String> items) {
        return new ArrayAdapter<String>(this, R.layout.spinner_item_with_icon, R.id.spinnerText, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ImageView icon = view.findViewById(R.id.spinnerIcon);
                if (icon != null) {
                    icon.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                ImageView icon = view.findViewById(R.id.spinnerIcon);
                if (icon != null) {
                    icon.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
                }
                return view;
            }
        };
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            profileImageView.setImageBitmap(bitmap);

                            // Optionally save image path to database
                            String imagePath = imageUri.toString();
                            // FIXED: Add null check for dbHelper
                            if (dbHelper != null) {
                                dbHelper.updateProfileImage(userEmail, imagePath);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void handleFormSubmission() {
        createAccountBtn.setOnClickListener(v -> {
            // Get input values (no username field needed)
            String fName = firstName.getText().toString().trim();
            String lName = lastName.getText().toString().trim();
            String phone = phoneNumber.getText().toString().trim();
            String province = spinnerProvince.getSelectedItem().toString().trim();
            String town = spinnerTown.getSelectedItem().toString().trim();
            String barangay = spinnerBarangay.getSelectedItem().toString().trim();
            String house = houseStreet.getText().toString().trim();

            Log.d("EditInfo", "Form submission started");
            Log.d("EditInfo", "First Name: " + fName);
            Log.d("EditInfo", "Last Name: " + lName);
            Log.d("EditInfo", "Phone: " + phone);
            Log.d("EditInfo", "Province: " + province);
            Log.d("EditInfo", "Town: " + town);
            Log.d("EditInfo", "Barangay: " + barangay);

            // Validate inputs
            if (!validateFormInputs(fName, lName, phone, province, town, barangay, house)) {
                Log.d("EditInfo", "Form validation failed");
                return;
            }

            Log.d("EditInfo", "Form validation passed, updating database");

            // FIXED: Add null check for dbHelper
            if (dbHelper == null) {
                Log.e("EditInfo", "DatabaseHelper is null!");
                Toast.makeText(this, "Database error. Please try again.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update user information in the database
            boolean isUpdated = dbHelper.updateUserInfo(userEmail, fName, lName, phone, province, town, barangay, house);
            Log.d("EditInfo", "Database update result: " + isUpdated);

            if (isUpdated) {
                Toast.makeText(this, "Profile completed successfully!", Toast.LENGTH_SHORT).show();

                // Log updated user info
                dbHelper.logAllUsers();

                Log.d("EditInfo", "About to navigate to Log_in");

                // Navigate to login screen
                Intent intent = new Intent(Edit_Info.this, Log_in.class);
                intent.putExtra("email", userEmail);
                intent.putExtra("profile_completed", true);
                startActivity(intent);
                finish();
            } else {
                Log.d("EditInfo", "Database update failed");
                Toast.makeText(this, "Failed to update profile. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateFormInputs(String fName, String lName, String phone,
                                       String province, String town, String barangay, String house) {
        // Check if required fields are empty
        if (fName.isEmpty() || lName.isEmpty() || phone.isEmpty() || house.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if dropdowns are selected properly
        if (province.equals("Choose your Province") ||
                town.equals("Choose your Town") ||
                barangay.equals("Choose your Barangay")) {
            Toast.makeText(this, "Please select Province, Town, and Barangay", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate phone number (basic validation)
        if (phone.length() < 10 || phone.length() > 15) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if phone number contains only digits and optional + at the beginning
        if (!phone.matches("^[+]?[0-9]+$")) {
            Toast.makeText(this, "Phone number should contain only numbers", Toast.LENGTH_SHORT).show();
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