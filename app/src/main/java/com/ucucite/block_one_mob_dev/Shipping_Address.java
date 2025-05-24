package com.ucucite.block_one_mob_dev;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
public class Shipping_Address extends AppCompatActivity {

    ImageView editIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_address); // loads shipping_address.xml

        editIcon = findViewById(R.id.edit_icon); // ID from shipping_address.xml

        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to EditAddressActivity
                Intent intent = new Intent(Shipping_Address.this, Edit_Address.class);
                startActivity(intent);
            }
        });
    }
}