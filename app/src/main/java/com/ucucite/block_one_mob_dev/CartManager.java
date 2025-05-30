package com.ucucite.block_one_mob_dev;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(Product product, int quantity) {
        // Check if product already exists in cart
        for (CartItem item : cartItems) {
            if (item.getProduct().getName().equals(product.getName())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        // If product doesn't exist, add new cart item
        cartItems.add(new CartItem(product, quantity));
    }

    public void removeFromCart(CartItem item) {
        cartItems.remove(item);
    }

    public void updateQuantity(CartItem item, int newQuantity) {
        if (newQuantity <= 0) {
            removeFromCart(item);
        } else {
            item.setQuantity(newQuantity);
        }
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public double getSubTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            String priceStr = item.getProduct().getPrice().replaceAll("[^\\d.]", "");
            try {
                double price = Double.parseDouble(priceStr);
                total += price * item.getQuantity();
            } catch (NumberFormatException e) {
                // Handle error
            }
        }
        return total;
    }

    public int getTotalItems() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getQuantity();
        }
        return total;
    }

    public void clearCart() {
        cartItems.clear();
    }
}