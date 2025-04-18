package com.example.agresstore.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("result")
    private String result;

    @SerializedName("data")
    private UserData data;

    @SerializedName("message")
    private String message;

    public String getResult() { return result; }
    public UserData getData() { return data; }
    public String getMessage() { return message; }

    public static class UserData {
        @SerializedName("nama")
        private String nama;
        @SerializedName("email")
        private String email;
        @SerializedName("status")
        private String status;
        @SerializedName("alamat")
        private String alamat;
        @SerializedName("kota")
        private String kota;
        @SerializedName("provinsi")
        private String provinsi;
        @SerializedName("telp")
        private long telp;
        @SerializedName("kodepos")
        private int kodepos;

        // Getters
        public String getNama() { return nama; }
        public String getEmail() { return email; }
        public String getStatus() { return status; }
        public String getAlamat() { return alamat; }
        public String getKota() { return kota; }
        public String getProvinsi() { return provinsi; }
        public long getTelp() { return telp; }
        public int getKodepos() { return kodepos; }
    }
}
