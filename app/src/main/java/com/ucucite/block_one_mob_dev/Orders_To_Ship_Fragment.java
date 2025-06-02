package com.ucucite.block_one_mob_dev;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

public class Orders_To_Ship_Fragment extends Fragment {

    private LinearLayout ordersContainer;
    private List<OrderItem> ordersList;

    // Simple OrderItem class to hold order data
    public static class OrderItem {
        public String productName;
        public String productBrand;
        public String productPrice;
        public int quantity;
        public int imageResource;
        public String paymentMethod;
        public String customerName;
        public String orderDate;

        public OrderItem(String name, String brand, String price, int qty, int image, String payment, String customer, String date) {
            this.productName = name;
            this.productBrand = brand;
            this.productPrice = price;
            this.quantity = qty;
            this.imageResource = image;
            this.paymentMethod = payment;
            this.customerName = customer;
            this.orderDate = date;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders__to__ship, container, false);

        ordersContainer = view.findViewById(R.id.orders_container); // You'll need to add this ID to your XML
        ordersList = new ArrayList<>();

        // Load saved orders
        loadOrders();

        return view;
    }

    // Method to add new order (call this from your checkout activities)
    public static void addNewOrder(String productName, String brand, String price, int quantity,
                                   int imageResource, String paymentMethod, String customerName) {
        // Save to SharedPreferences or database
        // For simplicity, we'll use a static list or SharedPreferences
        OrderDataManager.addOrder(productName, brand, price, quantity, imageResource, paymentMethod, customerName);
    }

    private void loadOrders() {
        // Load orders from SharedPreferences or database
        ordersList = OrderDataManager.getAllOrders(getContext());
        displayOrders();
    }

    private void displayOrders() {
        if (ordersContainer == null) return;

        ordersContainer.removeAllViews(); // Clear existing views

        if (ordersList.isEmpty()) {
            // Show empty state
            TextView emptyText = new TextView(getContext());
            emptyText.setText("No orders to ship");
            emptyText.setTextSize(16);
            emptyText.setPadding(32, 32, 32, 32);
            emptyText.setGravity(android.view.Gravity.CENTER);
            ordersContainer.addView(emptyText);
            return;
        }

        // Create cards for each order
        for (OrderItem order : ordersList) {
            createOrderCard(order);
        }
    }

    private void createOrderCard(OrderItem order) {
        // Inflate the order card layout
        View cardView = LayoutInflater.from(getContext()).inflate(R.layout.item_order_card, ordersContainer, false);

        // Find views
        ImageView productImage = cardView.findViewById(R.id.product_image);
        TextView productName = cardView.findViewById(R.id.product_name);
        TextView productBrand = cardView.findViewById(R.id.product_brand);
        TextView productPrice = cardView.findViewById(R.id.product_price);
        TextView productQuantity = cardView.findViewById(R.id.product_quantity);
        Button trackButton = cardView.findViewById(R.id.btn_track_order);

        // Set data
        if (productImage != null && order.imageResource != 0) {
            productImage.setImageResource(order.imageResource);
        }

        if (productName != null) {
            productName.setText(order.productName);
        }

        if (productBrand != null) {
            productBrand.setText(order.productBrand);
        }

        if (productPrice != null) {
            productPrice.setText(order.productPrice);
        }

        if (productQuantity != null) {
            productQuantity.setText("Quantity: " + order.quantity + " pcs");
        }

        if (trackButton != null) {
            trackButton.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Tracking order: " + order.productName, Toast.LENGTH_SHORT).show();
                // Add tracking functionality here
            });
        }

        // Add card to container
        ordersContainer.addView(cardView);
    }

    // Method to refresh orders when returning to fragment
    @Override
    public void onResume() {
        super.onResume();
        loadOrders(); // Refresh orders when fragment becomes visible
    }
}