package com.ucucite.block_one_mob_dev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

public class Swipe extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);

        // Set up ViewPager2
        ViewPager2 viewPager2 = findViewById(R.id.viewPager2);
        MyPagerAdapter adapter = new MyPagerAdapter(Swipe.this);
        viewPager2.setAdapter(adapter);
    }
}