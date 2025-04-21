package com.example.agresstore.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agresstore.R;
import com.example.agresstore.interfaces.CartUpdateInterface;
import com.example.agresstore.model.CartProduct;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;
    private List<CartProduct> cartProducts;
    private NumberFormat numberFormat;
    private SharedPreferences sharedPreferences;
    private CartUpdateInterface updateInterface;

    public CartAdapter(Context context, List<CartProduct> cartProducts, CartUpdateInterface listener) {
        this.context = context;
        this.cartProducts = cartProducts;
        this.updateInterface = listener;
        this.numberFormat = NumberFormat.getCurrencyInstance(new Locale("id","ID"));
        this.sharedPreferences = context.getSharedPreferences("checkout", Context.MODE_PRIVATE);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_cart,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartProduct item = cartProducts.get(position);
        holder.nama.setText(item.getNama());
        holder.harga.setText(formatPrice(item.getHarga()));
        holder.quantity.setText(String.valueOf(item.getQuantity()));
        holder.subtotal.setText(formatPrice(String.valueOf(Double.parseDouble(item.getHarga()) * item.getQuantity())));

        Glide.with(context)
                .load(item.getFoto())
                .into(holder.imageView);

        holder.btnDelete.setOnClickListener(view -> {
            removeItem(position);
            saveCartProducts();
            updateInterface.onCartUpdate(); // ini akan mentriger total harga
        });

        holder.btnIncrease.setOnClickListener(view -> {
            item.incrementQuantity();
            saveCartProducts();
            notifyItemChanged(position);
            updateInterface.onCartUpdate(); // ini akan mentriger total harga
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.decrementQuantity();
                saveCartProducts();
                notifyItemChanged(position);
                updateInterface.onCartUpdate(); // ini akan mentriger total harga
            }
        });
    }

    private void removeItem(int position) {
        cartProducts.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cartProducts.size());
        saveCartProducts();
    }

    private void saveCartProducts() {
        StringBuilder cartProductsStr = new StringBuilder();
        for (CartProduct item : cartProducts) {
            cartProductsStr.append(String.format("%s|%s|%s|%s|%d;",
                    item.getNama(),
                    item.getHarga(),
                    item.getStok(),
                    item.getFoto(),
                    item.getQuantity()
            ));
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cart_products", cartProductsStr.toString());
        editor.apply();
    }

    private String formatPrice(String price) {
        try {
            double amount = Double.parseDouble(price);
            return numberFormat.format(amount).replace("Rp", "Rp ");
        } catch (NumberFormatException e) {
            return "Rp " + price;
        }
    }

    @Override
    public int getItemCount() {
        return cartProducts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nama, harga, quantity, subtotal;
        ImageButton btnIncrease, btnDecrease, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cartItemImage);
            nama = itemView.findViewById(R.id.cartItemName);
            harga = itemView.findViewById(R.id.cartItemPrice);
            quantity = itemView.findViewById(R.id.tvQuantity);
            subtotal = itemView.findViewById(R.id.tvSubtotal);
            btnIncrease = itemView.findViewById(R.id.btnIncreaseQty);
            btnDecrease = itemView.findViewById(R.id.btnDecreaseQty);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
