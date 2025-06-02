package com.ucucite.block_one_mob_dev;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.util.List;

public class Cart_Fragment extends Fragment implements CartAdapter.OnCartItemClickListener {

    private static final int CHECKOUT_REQUEST_CODE = 1001;

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private TextView tvSubTotalValue;
    private TextView tvShippingValue;
    private TextView tvTotalValue;
    private Button btnCheckout;
    private Button btnApplyPromo;
    private EditText etPromoCode;
    private View emptyCartContainer;
    private View cartSummarySection;

    private CartManager cartManager;
    private final double SHIPPING_FEE = 100.00;
    private DecimalFormat df = new DecimalFormat("₱0.00");

    public Cart_Fragment() {
        // Required empty public constructor
    }

    public static Cart_Fragment newInstance() {
        return new Cart_Fragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cartManager = CartManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        updateCartDisplay();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCartDisplay(); // Refresh cart when fragment becomes visible
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_cart_items);
        tvSubTotalValue = view.findViewById(R.id.tvSubTotalValue);
        tvShippingValue = view.findViewById(R.id.tvShippingValue);
        tvTotalValue = view.findViewById(R.id.tvTotalValue);
        btnCheckout = view.findViewById(R.id.btn_checkout);
        btnApplyPromo = view.findViewById(R.id.btnApplyPromo);
        etPromoCode = view.findViewById(R.id.etPromoCode);
        emptyCartContainer = view.findViewById(R.id.empty_cart_container);
        cartSummarySection = view.findViewById(R.id.cart_summary_section);
    }

    private void setupRecyclerView() {
        List<CartItem> cartItems = cartManager.getCartItems();
        cartAdapter = new CartAdapter(cartItems, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(cartAdapter);
    }

    private void setupClickListeners() {
        if (btnCheckout != null) {
            btnCheckout.setOnClickListener(v -> {
                if (cartManager.getCartItems().isEmpty()) {
                    Toast.makeText(getContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show();
                } else {
                    navigateToCheckout();
                }
            });
        }

        if (btnApplyPromo != null) {
            btnApplyPromo.setOnClickListener(v -> {
                if (etPromoCode != null) {
                    String promoCode = etPromoCode.getText().toString().trim();
                    if (!promoCode.isEmpty()) {
                        Toast.makeText(getContext(), "Promo code applied: " + promoCode, Toast.LENGTH_SHORT).show();
                        // Implement promo code logic here
                    } else {
                        Toast.makeText(getContext(), "Please enter a promo code", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void navigateToCheckout() {
        try {
            List<CartItem> cartItems = cartManager.getCartItems();
            if (cartItems.isEmpty()) {
                Toast.makeText(getContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convert List to Array for Intent
            CartItem[] cartItemsArray = cartItems.toArray(new CartItem[0]);

            // Calculate totals
            double subtotal = cartManager.getSubTotal();
            double total = subtotal + SHIPPING_FEE;

            // Create Intent
            Intent checkoutIntent = new Intent(getActivity(), CartCheckout.class);

            // Add data to Intent
            checkoutIntent.putExtra("cart_items", cartItemsArray);
            checkoutIntent.putExtra("subtotal", subtotal);
            checkoutIntent.putExtra("shipping_fee", SHIPPING_FEE);
            checkoutIntent.putExtra("total", total);
            checkoutIntent.putExtra("checkout_message", "Proceeding to checkout with " + cartItems.size() + " items");

            // Start CartCheckout activity for result
            startActivityForResult(checkoutIntent, CHECKOUT_REQUEST_CODE);

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error navigating to checkout: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHECKOUT_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK && data != null) {
                // Checkout was successful
                boolean checkoutSuccess = data.getBooleanExtra("checkout_success", false);
                String paymentMethod = data.getStringExtra("payment_method");
                double orderTotal = data.getDoubleExtra("order_total", 0.0);

                if (checkoutSuccess) {
                    // Clear the cart after successful checkout
                    cartManager.clearCart();
                    updateCartDisplay();

                    Toast.makeText(getContext(),
                            "Order placed successfully!\nPayment: " + paymentMethod +
                                    "\nTotal: " + df.format(orderTotal),
                            Toast.LENGTH_LONG).show();
                }
            } else {
                // Checkout was cancelled or failed
                Toast.makeText(getContext(), "Checkout cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateCartDisplay() {
        List<CartItem> cartItems = cartManager.getCartItems();

        if (cartItems.isEmpty()) {
            showEmptyCart();
        } else {
            showCartWithItems();
            cartAdapter.updateCartItems(cartItems);
            updateTotals();
        }
    }

    private void showEmptyCart() {
        if (recyclerView != null) {
            recyclerView.setVisibility(View.GONE);
        }
        if (emptyCartContainer != null) {
            emptyCartContainer.setVisibility(View.VISIBLE);
        }
        if (cartSummarySection != null) {
            cartSummarySection.setVisibility(View.GONE);
        }
    }

    private void showCartWithItems() {
        if (recyclerView != null) {
            recyclerView.setVisibility(View.VISIBLE);
        }
        if (emptyCartContainer != null) {
            emptyCartContainer.setVisibility(View.GONE);
        }
        if (cartSummarySection != null) {
            cartSummarySection.setVisibility(View.VISIBLE);
        }
    }

    private void updateTotals() {
        double subTotal = cartManager.getSubTotal();
        double total = subTotal + SHIPPING_FEE;

        if (tvSubTotalValue != null) {
            tvSubTotalValue.setText(df.format(subTotal));
        }
        if (tvShippingValue != null) {
            tvShippingValue.setText(df.format(SHIPPING_FEE));
        }
        if (tvTotalValue != null) {
            tvTotalValue.setText(df.format(total));
        }
    }

    // CartAdapter.OnCartItemClickListener implementations
    @Override
    public void onIncreaseQuantity(CartItem item) {
        cartManager.updateQuantity(item, item.getQuantity() + 1);
        updateCartDisplay();
    }

    @Override
    public void onDecreaseQuantity(CartItem item) {
        if (item.getQuantity() > 1) {
            cartManager.updateQuantity(item, item.getQuantity() - 1);
        } else {
            cartManager.removeFromCart(item);
        }
        updateCartDisplay();
    }

    @Override
    public void onRemoveItem(CartItem item) {
        cartManager.removeFromCart(item);
        updateCartDisplay();
        Toast.makeText(getContext(), item.getProduct().getName() + " removed from cart",
                Toast.LENGTH_SHORT).show();
    }
}