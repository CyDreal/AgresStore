package com.example.agresstore;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agresstore.api.ServerAPI;
import com.example.agresstore.databinding.ActivityEditProfileBinding;
import com.example.agresstore.interfaces.EditProfile;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        loadCurrentData();
        setupClickListeners();
    }

    private void loadCurrentData() {
        binding.etName.setText(prefs.getString("nama", ""));
        binding.etAddress.setText(prefs.getString("alamat", ""));
        binding.etCity.setText(prefs.getString("kota", ""));
        binding.etProvince.setText(prefs.getString("provinsi", ""));
        binding.etPhone.setText(prefs.getString("telp", ""));
        binding.etPostal.setText(prefs.getString("kodepos", ""));
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSave.setOnClickListener(v -> {
           if (validateInput()) {
               updateProfile();
           }
        });
    }

    private boolean validateInput() {
        boolean isValid = true;

        if (binding.etName.getText().toString().trim().isEmpty()) {
            binding.tilName.setError("Name is required");
            isValid = false;
        }

        if (binding.etAddress.getText().toString().trim().isEmpty()) {
            binding.tilAddress.setError("Address is required");
            isValid = false;
        }

        if (binding.etCity.getText().toString().trim().isEmpty()) {
            binding.tilCity.setError("City is required");
            isValid = false;
        }

        if (binding.etProvince.getText().toString().trim().isEmpty()) {
            binding.tilProvince.setError("Province is required");
            isValid = false;
        }

        if (binding.etPhone.getText().toString().trim().isEmpty()) {
            binding.tilPhone.setError("Phone is required");
            isValid = false;
        }

        if (binding.etPostal.getText().toString().trim().isEmpty()) {
            binding.tilPostal.setError("Postal code is required");
            isValid = false;
        }

        return isValid;
    }

    private void updateProfile() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating profile...");
        progressDialog.show();

        String email = prefs.getString("email", "");
        String nama = binding.etName.getText().toString();
        String alamat = binding.etAddress.getText().toString();
        String kota = binding.etCity.getText().toString();
        String provinsi = binding.etProvince.getText().toString();
        String telp = binding.etPhone.getText().toString();
        String kodepos = binding.etPostal.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EditProfile service = retrofit.create(EditProfile.class);
        service.editProfile(email, nama, alamat, kota, provinsi, telp, kodepos)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        progressDialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                String jsonString = response.body().string();
                                Log.d("EditProfile", "Response: " + jsonString);
                                JSONObject jsonObject = new JSONObject(jsonString);

                                if (jsonObject.getInt("status") == 1) {
                                    // Update SharedPreferences
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("nama", nama);
                                    editor.putString("alamat", alamat);
                                    editor.putString("kota", kota);
                                    editor.putString("provinsi", provinsi);
                                    editor.putString("telp", telp);
                                    editor.putString("kodepos", kodepos);
                                    editor.apply();

                                    Toast.makeText(EditProfileActivity.this,
                                            jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(EditProfileActivity.this,
                                            jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.e("EditProfile", "Error Response: " + response.errorBody().string()); // Add this line
                                Toast.makeText(EditProfileActivity.this,
                                        "Update failed", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("EditProfile", "Exception: " + e.getMessage()); // Add this line
                            Toast.makeText(EditProfileActivity.this,
                                    "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e("EditProfile", "Network Error: " + t.getMessage()); // Add this line
                        Toast.makeText(EditProfileActivity.this,
                                "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}