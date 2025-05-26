package com.ucucite.block_one_mob_dev;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Profile_Fragment extends Fragment {

    private TextView tvName, tvUsername;
    private Button btnEditInfo;
    private DatabaseHelper dbHelper;

    private String email;

    public Profile_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(requireContext());

        // Get email from arguments
        if (getArguments() != null) {
            email = getArguments().getString("EMAIL");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvName = view.findViewById(R.id.tv_name);
        tvUsername = view.findViewById(R.id.tv_username);
        btnEditInfo = view.findViewById(R.id.btn_edit_profile);

        String usernameToDisplay = "user"; // Default fallback username

        if (email != null && !email.isEmpty()) {
            usernameToDisplay = getUsernameByEmail(email);
        }

        // Set tvName without @
        tvName.setText(usernameToDisplay);

        // Set tvUsername with @
        tvUsername.setText("@" + usernameToDisplay);

        btnEditInfo.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Edit_Info.class);
            intent.putExtra("EMAIL", email);  // Pass email for editing user info
            startActivity(intent);
        });
    }

    private String getUsernameByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String username = "user"; // fallback

        String[] columns = { "username" };
        String selection = "email = ?";
        String[] selectionArgs = { email };

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
