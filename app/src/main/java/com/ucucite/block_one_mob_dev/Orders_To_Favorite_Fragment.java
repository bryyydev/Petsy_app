package com.ucucite.block_one_mob_dev;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class Orders_To_Favorite_Fragment extends Fragment {

    public Orders_To_Favorite_Fragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders__to__favorite, container, false);
    }
}
