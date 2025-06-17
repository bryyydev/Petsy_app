package com.ucucite.block_one_mob_dev;

import android.content.Intent;
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
        public String orderID; // Added order ID
        public String fromAddress; // Added from address
        public String toAddress; // Added to address
        public String packageWeight; // Added package weight

        public OrderItem(String name, String brand, String price, int qty, int image, String payment, String customer, String date) {
            this.productName = name;
            this.productBrand = brand;
            this.productPrice = price;
            this.quantity = qty;
            this.imageResource = image;
            this.paymentMethod = payment;
            this.customerName = customer;
            this.orderDate = date;
            // Generate random order ID or set default values
            this.orderID = generateOrderID();
            // Fixed values for all orders
            this.fromAddress = "402 Bugayong Street Cabugao Molasagul Pangasinan Philippines";
            this.packageWeight = "500 grams";
            // Default customer address (this should ideally come from user profile)
            this.toAddress = "Block 2 Lot 17 Estrella Homes Phase 1 Toolong Kawit Cavite";
        }

        // Constructor with all parameters
        public OrderItem(String name, String brand, String price, int qty, int image, String payment,
                         String customer, String date, String orderID, String fromAddress, String toAddress, String weight) {
            this.productName = name;
            this.productBrand = brand;
            this.productPrice = price;
            this.quantity = qty;
            this.imageResource = image;
            this.paymentMethod = payment;
            this.customerName = customer;
            this.orderDate = date;
            this.orderID = orderID != null ? orderID : generateOrderID();
            // Fixed values for business
            this.fromAddress = "402 Bugayong Street Cabugao Molasagul Pangasinan Philippines";
            this.packageWeight = "500 grams";
            // Customer address can vary
            this.toAddress = toAddress != null ? toAddress : "Block 2 Lot 17 Estrella Homes Phase 1 Toolong Kawit Cavite";
        }

        private String generateOrderID() {
            return String.valueOf(System.currentTimeMillis() % 10000000000L);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders__to__ship, container, false);

        ordersContainer = view.findViewById(R.id.orders_container);
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
                // Navigate to Track_Orders activity and pass order data
                Intent intent = new Intent(getActivity(), Track_Orders.class);

                // Pass order data as extras
                intent.putExtra("product_name", order.productName);
                intent.putExtra("product_brand", order.productBrand);
                intent.putExtra("product_price", order.productPrice);
                intent.putExtra("product_quantity", order.quantity);
                intent.putExtra("product_image", order.imageResource);
                intent.putExtra("order_id", order.orderID);
                intent.putExtra("from_address", order.fromAddress);
                intent.putExtra("to_address", order.toAddress);
                intent.putExtra("package_weight", order.packageWeight);
                intent.putExtra("customer_name", order.customerName);
                intent.putExtra("order_date", order.orderDate);
                intent.putExtra("payment_method", order.paymentMethod);

                startActivity(intent);
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