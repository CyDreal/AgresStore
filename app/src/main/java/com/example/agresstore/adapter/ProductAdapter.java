package com.example.agresstore.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.agresstore.R;
import com.example.agresstore.model.DataProduct;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context context;
    private List<DataProduct> results;
    private NumberFormat numberFormat;
    private SharedPreferences sharedPreferences;

    public ProductAdapter(Context context, List<DataProduct> results) {
        this.context = context;
        this.results = results;
        this.numberFormat = NumberFormat.getCurrencyInstance(new Locale("id","ID"));
        this.sharedPreferences = context.getSharedPreferences("checkout", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view,parent,false);
        return new ProductAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        DataProduct result = results.get(position);
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
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView aMerk, aHarga, aStatus;
        ImageView aGambar;
        MaterialButton btnBuy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.aMerk = itemView.findViewById(R.id.tvProductName);
            this.aHarga = itemView.findViewById(R.id.tvPrice);
            this.aStatus = itemView.findViewById(R.id.chipStatus);
            this.aGambar = itemView.findViewById(R.id.ivProduct);
            this.btnBuy = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
