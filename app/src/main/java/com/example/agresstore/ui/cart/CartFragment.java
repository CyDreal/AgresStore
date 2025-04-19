package com.example.agresstore.ui.cart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.agresstore.adapter.CartAdapter;
import com.example.agresstore.databinding.FragmentCartBinding;
import com.example.agresstore.model.CartProduct;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private CartAdapter cartAdapter;
    private List<CartProduct> cartProducts;
    private SharedPreferences sharedPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        CartViewModel cartViewModel =
//                new ViewModelProvider(this).get(CartViewModel.class);

        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPreferences = requireContext().getSharedPreferences("checkout", Context.MODE_PRIVATE);
        cartProducts = new ArrayList<>();

        setupRecyclerView();
        return root;
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(requireContext(), cartProducts, () -> loadCartProducts());
        binding.cartRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.cartRecyclerView.setAdapter(cartAdapter);
    }

    private void loadCartProducts() {
        String cartProductsStr = sharedPreferences.getString("cart_products", "");
        cartProducts.clear();

        if (!cartProductsStr.isEmpty()) {
            String[] items = cartProductsStr.split(";");
            for (String item : items) {
                if (!item.isEmpty()) {
                    String[] parts = item.split("\\|");
                    if (parts.length == 5) {
                        CartProduct cartProduct = new CartProduct(
                                parts[0], // name
                                parts[1], // price
                                parts[2], // stock
                                parts[3], // image
                                Integer.parseInt(parts[4]) // quantity
                        );
                        cartProducts.add(cartProduct);
                    }
                }
            }
        }
        cartAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartProducts();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}