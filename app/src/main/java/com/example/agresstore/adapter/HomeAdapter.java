package com.example.agresstore.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.agresstore.ProductDetailActivity;
import com.example.agresstore.R;
import com.example.agresstore.model.DataProduct;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private Context context;
    private List<DataProduct> originalList; // semua data yang akan ditampilkan
    private List<DataProduct> filteredList; // data yang telah terfilter
    private NumberFormat numberFormat;
    private SharedPreferences sharedPreferences;
    private String lastQuery = ""; // untuk menyimpan query terakhir

    public HomeAdapter(Context context, List<DataProduct> results) {
        this.context = context;
        this.originalList = results;
        this.filteredList = new ArrayList<>(results);
        this.numberFormat = NumberFormat.getCurrencyInstance(new Locale("id","ID"));
        this.sharedPreferences = context.getSharedPreferences("checkout", Context.MODE_PRIVATE);
    }

    // method untuk melakukan filterisasi data
    public void filter(String query) {
        lastQuery = query; // menyimpan query terakhir
        // queary atau gampangnya text yang kita masukkan kedalam search view
        filteredList.clear();
        if (query.isEmpty()) {
            // jika query kosong, maka tampilkan semua data
            filteredList.addAll(originalList);
        } else {
            // mengkonvert / mengubah query menjadi huruf kecil guna menghindari case sensitive
            String lowercaseQuery = query.toLowerCase().trim();

            // filter item yang mengandung query
            for (DataProduct product : originalList) {
                if (product.getNama_produk().toLowerCase().contains(lowercaseQuery) ||
                product.getKategori().toLowerCase().contains(lowercaseQuery)) {
                    filteredList.add(product);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view,parent,false);
        return new HomeAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataProduct result = filteredList.get(position);
        holder.aMerk.setText(result.getNama_produk());

        // Format harga ke dalam bentuk Rupiah
        try {
            double harga = Double.parseDouble(result.getHarga_jual());
            String formattedPrice = numberFormat.format(harga);
            // Hapus simbol mata uang karena sudah termasuk "Rp"
            formattedPrice = formattedPrice.replace("Rp", "Rp ");
            holder.aHarga.setText(formattedPrice);
        } catch (NumberFormatException e) {
            holder.aHarga.setText("Rp " + result.getHarga_jual());
        }

        holder.aStatus.setText(result.getStatus());

        // Set chip background and text color based on status
        if ("Tersedia".equals(result.getStatus())) {
            holder.aStatus.setBackgroundResource(R.color.chip_bg_available);
            holder.aStatus.setTextColor(context.getColor(R.color.chip_text_available));
        } else {
            holder.aStatus.setBackgroundResource(R.color.chip_bg_unavailable);
            holder.aStatus.setTextColor(context.getColor(R.color.chip_text_unavailable));
        }

        Glide.with(context)
                .load(result.getFoto())
                .transition(DrawableTransitionOptions.withCrossFade())
//                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.aGambar);

        holder.btnBuy.setOnClickListener(view -> {
            try{
                String cartItems = sharedPreferences.getString("cart_products", "");
                boolean itemExist = false;
                StringBuilder updateCart = new StringBuilder();

                if (!cartItems.isEmpty()) {
                    String[] items = cartItems.split(";");
                    for (String item : items) {
                        if (!item.isEmpty()) {
                            String[] parts = item.split("\\|");
                            if (parts[0].equals(result.getNama_produk())) {
                                int qty = Integer.parseInt(parts[4]) + 1;
                                updateCart.append(String.format("%s|%s|%s|%s|%d;",
                                        parts[0], parts[1], parts[2], parts[3], qty));
                                itemExist = true;
                            } else {
                                updateCart.append(item).append(";");
                            }
                        }
                    }
                }
                if (!itemExist) {
                    updateCart.append(String.format("%s|%s|%s|%s|1;",
                            result.getNama_produk(),
                            result.getHarga_jual(),
                            result.getStatus(),
                            result.getFoto()));
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("cart_products", updateCart.toString());
                editor.apply();

                // Menggunakan Snackbar dengan view klik listener (v)
                Snackbar.make(view,
                        itemExist ? "Increased quantity for " + result.getNama_produk() :
                                "Added " + result.getNama_produk() + " to cart",
                        Snackbar.LENGTH_SHORT).show();
            } catch (Exception e) {
                // Snackbar untuk error handling
                Snackbar.make(view,
                        "Failed to add " + result.getNama_produk() + " to cart",
                        Snackbar.LENGTH_SHORT).show();
            }
        });

        // untuk mengambil data produk yang dipilih guna ditampilka pada product detail
        holder.itemView.setOnClickListener(v -> {
            DataProduct product = filteredList.get(position);
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            intent.putExtra("name", product.getNama_produk());
            intent.putExtra("price", product.getHarga_jual());
            intent.putExtra("status", product.getStatus());
            intent.putExtra("category", product.getKategori());
            intent.putExtra("stock", product.getStok());
            intent.putExtra("description", product.getDeskripsi());
            intent.putExtra("image", product.getFoto());
            context.startActivity(intent);
        });

        // Update visit count from database
        holder.tvVisitCount.setText(String.valueOf(result.getDilihat()));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView aMerk, aHarga, aStatus, tvVisitCount;
        ImageView aGambar;
        MaterialButton btnBuy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.aMerk = itemView.findViewById(R.id.tvProductName);
            this.aHarga = itemView.findViewById(R.id.tvPrice);
            this.aStatus = itemView.findViewById(R.id.chipStatus);
            this.aGambar = itemView.findViewById(R.id.ivProduct);
            this.btnBuy = itemView.findViewById(R.id.btnAddToCart);
            this.tvVisitCount = itemView.findViewById(R.id.tvVisitCount);
        }
    }

    public void filterByCategory(String category) {
        filteredList.clear();
        if (category == null || category.isEmpty()) {
            filteredList.addAll(originalList);
        } else {
            for (DataProduct product : originalList) {
                if (product.getKategori().equalsIgnoreCase(category)) {
                    filteredList.add(product);
                }
            }
        }
        notifyDataSetChanged();
    }

    // tambahkan method untuk mendapatkan queary terakhir
    public String getLastQuery() {
        return lastQuery;
    }
}
