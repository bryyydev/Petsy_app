package com.ucucite.block_one_mob_dev;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderDataManager {
    private static final String PREFS_NAME = "OrdersData";
    private static final String ORDERS_KEY = "orders_list";

    // Add new order
    public static void addOrder(String productName, String brand, String price, int quantity,
                                int imageResource, String paymentMethod, String customerName) {
        // This is a simple static method - in a real app you'd pass context
        // For school project, we'll use a different approach in the checkout activities
    }

    // Add order with context
    public static void addOrder(Context context, String productName, String brand, String price,
                                int quantity, int imageResource, String paymentMethod, String customerName) {
        List<Orders_To_Ship_Fragment.OrderItem> orders = getAllOrders(context);

        String currentDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());

        Orders_To_Ship_Fragment.OrderItem newOrder = new Orders_To_Ship_Fragment.OrderItem(
                productName, brand, price, quantity, imageResource, paymentMethod, customerName, currentDate
        );

        orders.add(newOrder);
        saveOrders(context, orders);
    }

    // Get all orders
    public static List<Orders_To_Ship_Fragment.OrderItem> getAllOrders(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String ordersJson = prefs.getString(ORDERS_KEY, "");

        if (ordersJson.isEmpty()) {
            return new ArrayList<>();
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Orders_To_Ship_Fragment.OrderItem>>(){}.getType();

        try {
            return gson.fromJson(ordersJson, listType);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // Save orders to SharedPreferences
    private static void saveOrders(Context context, List<Orders_To_Ship_Fragment.OrderItem> orders) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String ordersJson = gson.toJson(orders);

        editor.putString(ORDERS_KEY, ordersJson);
        editor.apply();
    }

    // Clear all orders (for testing)
    public static void clearAllOrders(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}