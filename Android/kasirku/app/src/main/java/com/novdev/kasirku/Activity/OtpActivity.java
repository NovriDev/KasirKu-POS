package com.novdev.kasirku.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.novdev.kasirku.DB.TokenDBHelper;
import com.novdev.kasirku.Request.OtpVerifyRequest;
import com.novdev.kasirku.Retrofit.ApiClient;
import com.novdev.kasirku.Retrofit.ApiService;
import com.novdev.kasirku.databinding.ActivityOtpBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {
    ActivityOtpBinding binding;
    String email;
    String from; // "register" atau "login"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Ambil data dari intent
        email = getIntent().getStringExtra("email");
        from = getIntent().getStringExtra("from");

        binding.start.setOnClickListener(v -> {
            String otp = binding.otp.getText().toString();

            if (TextUtils.isEmpty(otp)) {
                binding.otp.setError("OTP wajib diisi");
                return;
            }

            if (from.equals("register")) {
                verifyRegisterOtp(email, otp);
            } else if (from.equals("login")) {
                verifyLoginOtp(email, otp);
            }
        });
    }

    private void verifyRegisterOtp(String email, String otp) {
        OtpVerifyRequest request = new OtpVerifyRequest(email, otp);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.verifyOtp(request).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String token = response.body().get("token").getAsString();
                    new TokenDBHelper(OtpActivity.this).saveToken(token);
                    Toast.makeText(OtpActivity.this, "OTP Register Berhasil!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(OtpActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(OtpActivity.this, "OTP Gagal: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(OtpActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyLoginOtp(String email, String otp) {
        OtpVerifyRequest request = new OtpVerifyRequest(email, otp);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.verifyOtpLogin(request).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String token = response.body().get("token").getAsString();
                    new TokenDBHelper(OtpActivity.this).saveToken(token);
                    Toast.makeText(OtpActivity.this, "OTP Login Berhasil!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(OtpActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(OtpActivity.this, "OTP Gagal: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(OtpActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
