package com.ucucite.block_one_mob_dev;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
    private EditText username, firstName, lastName, phoneNumber, houseStreet;
    private Spinner spinnerProvince, spinnerTown, spinnerBarangay;
    private Button createAccountBtn;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private final Map<String, List<String>> provinceTownMap = new HashMap<>();
    private final Map<String, List<String>> townBarangayMap = new HashMap<>();

    private DatabaseHelper dbHelper;
    private String userEmail = "test@example.com"; // Replace with actual session email if available

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupDropdownMaps();
        setupDropdowns();
        setupImagePicker();
        handleFormSubmission();

        dbHelper.logAllUsers();
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

        editIcon.setOnClickListener(v -> pickImageFromGallery());
    }

    private void setupDropdownMaps() {
        provinceTownMap.put("Ilocos Norte", Arrays.asList("Laoag City", "Batac", "Paoay"));
        provinceTownMap.put("Ilocos Sur", Arrays.asList("Vigan City", "Candon", "Narvacan"));
        provinceTownMap.put("La Union", Arrays.asList("San Fernando City", "Agoo", "Bauang"));
        provinceTownMap.put("Pangasinan", Arrays.asList("Dagupan City", "Urdaneta", "Alaminos"));

        townBarangayMap.put("Laoag City", Arrays.asList("Barangay 1", "Barangay 2", "Barangay 3"));
        townBarangayMap.put("Batac", Arrays.asList("Barangay A", "Barangay B"));
        townBarangayMap.put("Vigan City", Arrays.asList("Barangay I", "Barangay II"));
        townBarangayMap.put("Dagupan City", Arrays.asList("Lucao", "Pantal", "Bonuan Gueset"));
    }

    private void setupDropdowns() {
        List<String> provinceList = new ArrayList<>();
        provinceList.add("Choose your Province");
        provinceList.addAll(provinceTownMap.keySet());
        spinnerProvince.setAdapter(getCustomAdapter(provinceList));

        spinnerTown.setAdapter(getCustomAdapter(Collections.singletonList("Choose your Town")));
        spinnerBarangay.setAdapter(getCustomAdapter(Collections.singletonList("Choose your Barangay")));

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

                        spinnerBarangay.setAdapter(getCustomAdapter(Collections.singletonList("Choose your Barangay")));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

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
                icon.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                ImageView icon = view.findViewById(R.id.spinnerIcon);
                icon.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
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
                            // You could save image path to DB here
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
            String uname = username.getText().toString().trim();
            String fName = firstName.getText().toString().trim();
            String lName = lastName.getText().toString().trim();
            String phone = phoneNumber.getText().toString().trim();
            String province = spinnerProvince.getSelectedItem().toString().trim();
            String town = spinnerTown.getSelectedItem().toString().trim();
            String barangay = spinnerBarangay.getSelectedItem().toString().trim();
            String house = houseStreet.getText().toString().trim();

            if (uname.isEmpty() || fName.isEmpty() || lName.isEmpty() || phone.isEmpty() || house.isEmpty() ||
                    province.equals("Choose your Province") ||
                    town.equals("Choose your Town") ||
                    barangay.equals("Choose your Barangay")) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isUpdated = dbHelper.updateUserInfo(userEmail, fName, lName, phone, province, town, barangay, house);

            if (isUpdated) {
                Toast.makeText(this, "Information updated successfully!", Toast.LENGTH_SHORT).show();

                // Navigate to profile.java
                Intent intent = new Intent(Edit_Info.this, Profile_Fragment.class);
                intent.putExtra("email", userEmail); // optional: pass email
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to update information", Toast.LENGTH_SHORT).show();
            }
        });
    }
}