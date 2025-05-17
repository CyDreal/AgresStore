package com.example.agresstore.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.agresstore.R;
import com.example.agresstore.adapter.HomeAdapter;
import com.example.agresstore.databinding.FragmentHomeBinding;
import com.example.agresstore.model.DataProduct;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private HomeAdapter adapter;
    private List<DataProduct> productList = new ArrayList<>();
    private String lastQuery = ""; // untuk menyimpan query terakhir

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        setupUserName();
        setupViewModel();
        setupCategories();
        setupRecyclerView();
        setupSearchView();
        setupImageSlider();

        return binding.getRoot();
    }

    private void setupImageSlider() {
        ImageSlider imageSlider = binding.imageSlider;

        // membuat list untuk slide images
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        // menambahkan gambar dari drawable
        slideModels.add(new SlideModel(R.drawable.store_front,  ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.store_inside, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.store_header, ScaleTypes.FIT));

        // set images ke image slider
        imageSlider.setImageList(slideModels,ScaleTypes.FIT);
    }

    private void setupUserName() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userName = prefs.getString("nama", "User");
        binding.tvUserName.setText(userName);
    }

    private void setupCategories() {
        // chip = button untuk kategorinya
        // Bersihkan chip group terlebih dahulu
        binding.chipGroupCategories.removeAllViews();

        // Tambahkan chip "All" kembali
        Chip allChip = new Chip(requireContext());
        allChip.setText("All");
        allChip.setCheckable(true);
        allChip.setChecked(true);
        binding.chipGroupCategories.addView(allChip);

        // mengambil kategori unik dari data produk
        Set<String> uniqueCategories = new HashSet<>();
        for (DataProduct product : productList) {
            uniqueCategories.add(product.getKategori());
        }

        // membuat chip untuk setiap kategori
        for (String category : uniqueCategories) {
            Chip chip = new Chip(requireContext());
            chip.setText(category);
            chip.setCheckable(true);
            chip.setClickable(true);

            // handle saat chip ditklik
            chip.setOnClickListener(v -> {
                if (chip.isChecked()) {
                    adapter.filterByCategory(category);
                } else {
                    adapter.filterByCategory(null); // Show all
                }
            });

            binding.chipGroupCategories.addView(chip);
        }

        // Handle "All" chip
        binding.chipAll.setOnClickListener(v -> {
            if (binding.chipAll.isChecked()) {
                adapter.filterByCategory(null); // Show all products
                // Uncheck other chips
                for (int i = 1; i < binding.chipGroupCategories.getChildCount(); i++) {
                    ((Chip) binding.chipGroupCategories.getChildAt(i)).setChecked(false);
                }
            }
        });
    }


    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.rViewProduct;
        adapter = new HomeAdapter(requireContext(), productList);

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
                lastQuery = query; // menyimpan query terakhir
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                lastQuery = newText; // menyimpan query terakhir
                // melakukan filterisasi secara realtime saat user sedang mengetik
                adapter.filter(newText);
                return false;
            }
        });

        // set query yang tersimpan jika ada
        if (!lastQuery.isEmpty()) {
            binding.searchView.setQuery(lastQuery, false);
            binding.searchView.clearFocus();
        }

        // optional: Customize SearchView
        binding.searchView.setIconifiedByDefault(true);
        binding.searchView.setQueryHint("Cari Produk...");
    }

    private void setupViewModel() {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                productList.clear();
                productList.addAll(products);
                adapter = new HomeAdapter(requireContext(), products);
                binding.rViewProduct.setAdapter(adapter);

                // menerapkan filter terakhir jika ada
                if (!lastQuery.isEmpty()) {
                    adapter.filter(lastQuery);
                }

                // memindahkan setup kategories ke sini setelah data dimuat
                setupCategories();
            }
        });
        homeViewModel.loadProducts();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload products when returning to fragment
        homeViewModel.loadProducts();
        // terapkan kembali filter saat fragment di-resume
        if (adapter != null && !lastQuery.isEmpty()) {
            adapter.filter(lastQuery);
        }
    }

    // simpan state saat fragment dihancurkan
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("lastQuery", lastQuery);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            lastQuery = savedInstanceState.getString("lastQuery", "");
        }
    }

}