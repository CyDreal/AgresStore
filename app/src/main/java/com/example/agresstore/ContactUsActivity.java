package com.example.agresstore;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.agresstore.databinding.ActivityContactUsBinding;
import com.google.android.material.button.MaterialButton;

public class ContactUsActivity extends AppCompatActivity {

    private ActivityContactUsBinding binding;
    MaterialButton btnCall, btnEmail, btnInstagram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        btnCall = findViewById(R.id.btn_call);

        btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:081297009800")); // Ganti dengan nomor yang diinginkan
            startActivity(intent);
        });
//
        btnEmail = findViewById(R.id.btn_email); // Beri ID di XML
        btnEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:sales@agres.id")); // Ganti dengan email tujuan
            intent.putExtra(Intent.EXTRA_SUBJECT, "Customer Inquiry");
            intent.putExtra(Intent.EXTRA_TEXT, "Hello AgresStore Team,\n\n");

            try {
                startActivity(Intent.createChooser(intent, "Send Email"));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No email app installed", Toast.LENGTH_SHORT).show();
            }
        });
//
        btnInstagram = findViewById(R.id.btn_instagram);
        btnInstagram.setOnClickListener(v -> {
            String instagramUrl = "http://instagram.com/agresstore";
            try {
                // Mencoba membuka aplikasi Instagram
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                if (intent != null) {
                    // Instagram terinstall, buka URL di Instagram
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, instagramUrl);
                    startActivity(intent);
                } else {
                    // Instagram tidak terinstall, buka di browser
                    Intent browserIntent = new Intent();
                    browserIntent.setAction(Intent.ACTION_MAIN);
                    browserIntent.addCategory(Intent.CATEGORY_APP_BROWSER);
                    browserIntent.setData(Uri.parse(instagramUrl));
                    startActivity(browserIntent);
                }
            } catch (Exception e) {
                // Fallback ke browser default jika terjadi error
                Intent browserIntent = new Intent();
                browserIntent.setAction(Intent.ACTION_MAIN);
                browserIntent.addCategory(Intent.CATEGORY_APP_BROWSER);
                browserIntent.setData(Uri.parse(instagramUrl));
                startActivity(browserIntent);
            }
        });
    }
}