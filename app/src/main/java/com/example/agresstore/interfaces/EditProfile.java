package com.example.agresstore.interfaces;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface EditProfile {
    @FormUrlEncoded
    @POST("edit_profile.php")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Call<ResponseBody> editProfile(
            @Field("email") String email,
            @Field("nama") String nama,
            @Field("alamat") String alamat,
            @Field("kota") String kota,
            @Field("provinsi") String provinsi,
            @Field("telp") String telp,
            @Field("kodepos") String kodepos
    );
}