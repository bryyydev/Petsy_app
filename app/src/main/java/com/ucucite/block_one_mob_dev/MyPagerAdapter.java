package com.ucucite.block_one_mob_dev;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.fragment.app.FragmentActivity;

public class MyPagerAdapter extends FragmentStateAdapter {

    public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the fragment based on the position
        if (position == 0) {
            return new First_Fragment(); // obs_one
        } else if (position == 1) {
            return new Second_Fragment(); // obs_two
        } else {
            return new Third_Fragment(); // obs_three
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Number of fragments (obs_one + obs_two)
    }
}