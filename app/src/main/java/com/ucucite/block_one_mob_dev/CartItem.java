package com.ucucite.block_one_mob_dev;

public class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        String priceStr = product.getPrice().replaceAll("[^\\d.]", "");
        try {
            double price = Double.parseDouble(priceStr);
            return price * quantity;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}