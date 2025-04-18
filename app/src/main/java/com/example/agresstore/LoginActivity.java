package com.example.agresstore;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.agresstore.api.ServerAPI;
import com.example.agresstore.interfaces.Login;
import com.example.agresstore.model.LoginResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    public static final String URL = ServerAPI.BASE_URL;
    private ProgressDialog pd;
    private AppCompatButton btnLogin;
    private TextInputEditText etEmail, etPassword;
    private TextView tvRegister;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserSession";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Check if user is already logged in
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

       // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvRegister = findViewById(R.id.tvRegister);
        btnLogin = findViewById(R.id.btnLogin);

        // Initialize progress dialog
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);

        tvRegister.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        btnLogin.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                showAlert("Email and password are required");
                return;
            }

            prosesLogin(email, password);
        });
    }

    private void prosesLogin(String email, String password) {
        pd.show();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Login api = retrofit.create(Login.class);
        api.login(email, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                pd.dismiss();
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();
                        if ("1".equals(loginResponse.getResult())) {
                            LoginResponse.UserData userData = loginResponse.getData();
                            if (userData != null && "1".equals(userData.getStatus())) {
                                // Save user data to SharedPreferences
                                saveUserData(userData);

                                Toast.makeText(LoginActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                showAlert("Status User Ini Tidak Aktif");
                            }
                        } else {
                            showAlert("Email Atau Password Salah");
                        }
                    } else {
                        showAlert("Server error: " + response.code());
                    }
                } catch (Exception e) {
                    Log.e("LoginError", "Error processing response", e);
                    showAlert("Error processing response: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                pd.dismiss();
                Log.e("LoginError", "Network error", t);
                showAlert("Connection failed: " + t.getMessage());
            }
        });
    }

    private void saveUserData(LoginResponse.UserData userData) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("nama", userData.getNama());
        editor.putString("email", userData.getEmail());
        editor.putString("status", userData.getStatus());

        // Add the additional fields from your JSON response
        editor.putString("alamat", userData.getAlamat());
        editor.putString("kota", userData.getKota());
        editor.putString("provinsi", userData.getProvinsi());
        editor.putString("telp", String.valueOf(userData.getTelp()));
        editor.putString("kodepos", String.valueOf(userData.getKodepos()));

        editor.apply();
    }

    private void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .create()
                .show();
    }

}