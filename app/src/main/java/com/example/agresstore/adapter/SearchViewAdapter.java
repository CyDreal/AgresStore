package com.example.agresstore.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agresstore.R;

import java.util.ArrayList;

public class SearchViewAdapter extends RecyclerView.Adapter<SearchViewAdapter.ViewHolder> {

    private ArrayList<String> localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tvName);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    public SearchViewAdapter(ArrayList<String> dataSet) {
        localDataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        // membuat view baru yang mendefinisikan UI dari list item
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.list_view, parent, false);
        return new ViewHolder(view);
    }

    // mengganti konten dari suatu View
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // mengambil elemen dari dataset pada posisi tertentu dan mennganti kontennya
        viewHolder.getTextView().setText(localDataSet.get(position));
    }

    // mengembalikan ukuran dataset
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

}
