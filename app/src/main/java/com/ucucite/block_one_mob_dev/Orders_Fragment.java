package com.ucucite.block_one_mob_dev;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class Orders_Fragment extends Fragment {

    private Button btnToPay, btnToShip, btnToReceive, btnCompleted;
    private View rootView;
    private Fragment currentFragment;
    private static final String SELECTED_TAB_KEY = "selected_tab";
    private int selectedTabIndex = 0;

    private static final String TAG = "OrdersFragment";
    private static final String TAG_TO_PAY = "to_pay_fragment";
    private static final String TAG_TO_SHIP = "to_ship_fragment";
    private static final String TAG_TO_RECEIVE = "to_receive_fragment";
    private static final String TAG_COMPLETED = "completed_fragment";

    public Orders_Fragment() {}

    public static Orders_Fragment newInstance() {
        return new Orders_Fragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            selectedTabIndex = savedInstanceState.getInt(SELECTED_TAB_KEY, 0);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_orders, container, false);
        initializeViews();
        setupClickListeners();
        loadFragmentByIndex(selectedTabIndex);
        return rootView;
    }

    private void initializeViews() {
        btnToPay = rootView.findViewById(R.id.btn_to_pay);
        btnToShip = rootView.findViewById(R.id.btn_to_ship);
        btnToReceive = rootView.findViewById(R.id.btn_to_receive);
        btnCompleted = rootView.findViewById(R.id.btn_completed);
    }

    private void setupClickListeners() {
        btnToPay.setOnClickListener(v -> {
            if (selectedTabIndex != 0) {
                selectedTabIndex = 0;
                loadChildFragment(new Orders_To_Pay_Fragment(), TAG_TO_PAY);
                updateButtonStates();
            }
        });

        btnToShip.setOnClickListener(v -> {
            if (selectedTabIndex != 1) {
                selectedTabIndex = 1;
                loadChildFragment(new Orders_To_Ship_Fragment(), TAG_TO_SHIP);
                updateButtonStates();
            }
        });

        btnToReceive.setOnClickListener(v -> {
            if (selectedTabIndex != 2) {
                selectedTabIndex = 2;
                loadChildFragment(new Orders_To_Receive_Fragment(), TAG_TO_RECEIVE);
                updateButtonStates();
            }
        });

        btnCompleted.setOnClickListener(v -> {
            if (selectedTabIndex != 3) {
                selectedTabIndex = 3;
                loadChildFragment(new Orders_Completed_Fragment(), TAG_COMPLETED);
                updateButtonStates();
            }
        });
    }

    private void loadFragmentByIndex(int index) {
        Fragment fragment;
        String tag;

        switch (index) {
            case 1:
                fragment = new Orders_To_Ship_Fragment();
                tag = TAG_TO_SHIP;
                break;
            case 2:
                fragment = new Orders_To_Receive_Fragment();
                tag = TAG_TO_RECEIVE;
                break;
            case 3:
                fragment = new Orders_Completed_Fragment();
                tag = TAG_COMPLETED;
                break;
            case 0:
            default:
                fragment = new Orders_To_Pay_Fragment();
                tag = TAG_TO_PAY;
                selectedTabIndex = 0;
                break;
        }

        loadChildFragment(fragment, tag);
        updateButtonStates();
    }

    private void loadChildFragment(Fragment fragment, String tag) {
        if (getActivity() == null || isDetached()) return;

        Log.d(TAG, "Loading fragment with tag: " + tag);

        try {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            transaction.replace(R.id.orders_fragment_container, fragment, tag);
            transaction.commitAllowingStateLoss();
            currentFragment = fragment;
        } catch (Exception e) {
            Log.e(TAG, "Error loading fragment: " + tag, e);
        }
    }

    private void updateButtonStates() {
        if (getContext() == null) return;

        resetButtonState(btnToPay);
        resetButtonState(btnToShip);
        resetButtonState(btnToReceive);
        resetButtonState(btnCompleted);

        Button selectedButton = getSelectedButton();
        if (selectedButton != null) {
            setSelectedButtonState(selectedButton);
        }
    }

    private Button getSelectedButton() {
        switch (selectedTabIndex) {
            case 0: return btnToPay;
            case 1: return btnToShip;
            case 2: return btnToReceive;
            case 3: return btnCompleted;
            default: return btnToPay;
        }
    }

    private void resetButtonState(Button button) {
        if (getContext() != null && button != null) {
            button.setBackgroundResource(R.drawable.button_outline);
            button.setTextColor(ContextCompat.getColor(getContext(), R.color.text_secondary));
        }
    }

    private void setSelectedButtonState(Button button) {
        if (getContext() != null && button != null) {
            button.setBackgroundResource(R.drawable.green_button_bg);
            button.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_TAB_KEY, selectedTabIndex);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        btnToPay = null;
        btnToShip = null;
        btnToReceive = null;
        btnCompleted = null;
        rootView = null;
        currentFragment = null;
    }

    public void switchToTab(int tabIndex) {
        if (tabIndex >= 0 && tabIndex <= 3 && tabIndex != selectedTabIndex) {
            selectedTabIndex = tabIndex;
            loadFragmentByIndex(tabIndex);
        }
    }

    public int getCurrentTabIndex() {
        return selectedTabIndex;
    }
}
