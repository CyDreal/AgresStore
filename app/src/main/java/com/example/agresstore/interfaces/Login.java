package com.example.agresstore.interfaces;

import com.example.agresstore.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Login {
    @FormUrlEncoded
    @POST("get_login.php")
    Call<LoginResponse> login(
            @Field("email") String email,
            @Field("password") String password
    );

}
