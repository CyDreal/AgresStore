package com.example.agresstore.interfaces;

import com.example.agresstore.model.ViewCountResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ViewCount {
    @FormUrlEncoded
    @POST("update_view_count.php")
    Call<ViewCountResponse> updateViewCount(@Field("product_id") String productId);
}