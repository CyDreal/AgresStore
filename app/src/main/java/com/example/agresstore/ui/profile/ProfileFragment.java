package com.example.agresstore.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.agresstore.ContactUsActivity;
import com.example.agresstore.EditProfileActivity;
import com.example.agresstore.LoginActivity;
import com.example.agresstore.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);

        loadUserProfile();
        setupClickListeners();
        return binding.getRoot();
    }

    private void loadUserProfile() {
        SharedPreferences prefs = requireContext().getSharedPreferences("UserSession", requireContext().MODE_PRIVATE);

        binding.tvName.setText(prefs.getString("nama", "-"));
        binding.tvEmail.setText(prefs.getString("email", "-"));
        binding.tvPhone.setText(prefs.getString("telp", "-"));
        binding.tvAddress.setText(prefs.getString("alamat", "-"));
        binding.tvCity.setText(prefs.getString("kota", "-"));
        binding.tvProvince.setText(prefs.getString("provinsi", "-"));
        binding.tvPostalCode.setText(prefs.getString("kodepos", "-"));
    }

    private void setupClickListeners() {
        binding.btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), EditProfileActivity.class);
            startActivity(intent);
        });

        binding.btnContactUs.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ContactUsActivity.class);
            startActivity(intent);
        });

        binding.btnLogout.setOnClickListener(v -> {
            logout();
        });
    }

    private void logout() {
        // bersihkan semua data yang ada di UserSession
        SharedPreferences prefs = requireContext().getSharedPreferences("UserSession", requireContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        // menampilkan pesan sukses
        Toast.makeText(requireContext(), "Logout successful", Toast.LENGTH_SHORT).show();

        // arahkan ke aktivity login dan bersihkan stack
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserProfile(); // mengambil kembali data saat kembali dari edit profile
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}