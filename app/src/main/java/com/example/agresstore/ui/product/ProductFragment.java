package com.example.agresstore.ui.product;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agresstore.adapter.ProductAdapter;
import com.example.agresstore.databinding.FragmentProductBinding;
import com.example.agresstore.model.DataProduct;

import java.util.ArrayList;
import java.util.List;

public class ProductFragment extends Fragment {

    private FragmentProductBinding binding;
    private ProductViewModel productViewModel;
    private ProductAdapter adapter;
    private List<DataProduct> productList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        productViewModel =
//                new ViewModelProvider(this).get(ProductViewModel.class);

        binding = FragmentProductBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();

        // hapus duplikasi atau redudansi dari instalasi ViewModel
//        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        setupRecyclerView();
        setupSearchView();
        setupViewModel();
//        observeProducts();
//        productViewModel.loadProducts(); // memuat data dari API

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.rViewProduct;
        adapter = new ProductAdapter(requireContext(), productList);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.left = 10;
                outRect.right = 10;
                outRect.top = 5;
                outRect.bottom = 5;
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });

        // optional: Customize SearchView
        binding.searchView.setIconifiedByDefault(true);
        binding.searchView.setQueryHint("Cari Produk...");
    }

    private void setupViewModel() {
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        productViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                productList.clear();
                productList.addAll(products);
                adapter = new ProductAdapter(requireContext(), products);
                binding.rViewProduct.setAdapter(adapter);
            }
        });
        productViewModel.loadProducts();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

//    private void observeProducts() {
//        // Fix the parameter naming conflict
//        productViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
//            if(products != null) {
//                productList.clear();
//                productList.addAll(products); // Use products instead of productList
//                adapter.notifyDataSetChanged();
//            }
//        });
//    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
}