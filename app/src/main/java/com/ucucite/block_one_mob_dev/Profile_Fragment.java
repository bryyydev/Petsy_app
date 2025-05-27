package com.ucucite.block_one_mob_dev;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Profile_Fragment extends Fragment {

    private TextView tvName, tvUsername;
    private Button btnEditInfo;
    private LinearLayout shippingAddressItem, personalInfoItem, paymentMethodsItem;
    private DatabaseHelper dbHelper;
    private String email;

    public Profile_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(requireContext());
        if (getArguments() != null) {
            email = getArguments().getString("EMAIL");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        tvName = view.findViewById(R.id.tv_name);
        tvUsername = view.findViewById(R.id.tv_username);
        btnEditInfo = view.findViewById(R.id.btn_edit_profile);
        shippingAddressItem = view.findViewById(R.id.shipping_address_item);
        personalInfoItem = view.findViewById(R.id.personal_info_item);
        paymentMethodsItem = view.findViewById(R.id.payment_methods_item);

        // Load user data
        if (email != null && !email.isEmpty()) {
            String fullName = getFullNameByEmail(email);
            String username = getUsernameByEmail(email);
            tvName.setText(fullName);
            tvUsername.setText("@" + username);
        }

        // Set click listeners
        btnEditInfo.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Edit_Info.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // Add click listener for shipping address
        shippingAddressItem.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Shipping_Address.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // Add click listener for personal information
        personalInfoItem.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Edit_Info.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // Add click listener for payment methods (if you have a Payment activity)
        paymentMethodsItem.setOnClickListener(v -> {
            // Uncomment when you have a Payment Methods activity
            // Intent intent = new Intent(getActivity(), Payment_Methods.class);
            // intent.putExtra("email", email);
            // startActivity(intent);
        });
    }

    private String getFullNameByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String fullName = "User";
        String[] columns = {"first_name", "last_name"};
        String selection = "email = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query("users", columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
            fullName = firstName + " " + lastName;
            cursor.close();
        }
        return fullName;
    }

    private String getUsernameByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String username = "user";
        String[] columns = {"username"};
        String selection = "email = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query("users", columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            cursor.close();
        }
        return username;
    }

    @Override
    public void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}