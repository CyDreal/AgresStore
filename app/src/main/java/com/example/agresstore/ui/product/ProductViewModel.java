package com.example.agresstore.ui.product;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.agresstore.api.ServerAPI;
import com.example.agresstore.interfaces.Product;
import com.example.agresstore.model.DataProduct;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductViewModel extends ViewModel {

    public static final String URL = ServerAPI.BASE_URL;

    private MutableLiveData<List<DataProduct>> products;

    public ProductViewModel() {
        products = new MutableLiveData<List<DataProduct>>();
    }

    public LiveData<List<DataProduct>> getProducts() {
        return products;
    }

    public void loadProducts() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Product api = retrofit.create(Product.class);
        Call<List<DataProduct>> call = api.view();

        call.enqueue(new Callback<List<DataProduct>>() {
            @Override
            public void onResponse(Call<List<DataProduct>> call, Response<List<DataProduct>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    products.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<DataProduct>> call, Throwable throwable) {
                Log.e("ProductViewModel", "Error loading products" + throwable.getMessage());
            }
        });
    }

}