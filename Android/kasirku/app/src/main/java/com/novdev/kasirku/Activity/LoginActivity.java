package com.novdev.kasirku.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.novdev.kasirku.DB.TokenDBHelper;
import com.novdev.kasirku.R;
import com.novdev.kasirku.Request.LoginRequest;
import com.novdev.kasirku.Retrofit.ApiClient;
import com.novdev.kasirku.Retrofit.ApiService;
import com.novdev.kasirku.databinding.ActivityLoginBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    private boolean isPasswordVisible = false;
    EditText Eemail, Epassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.showPassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Sembunyikan password
                binding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.showPassword.setImageResource(R.drawable.ic_not_show_pw);
                isPasswordVisible = false;
            } else {
                // Tampilkan password
                binding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                binding.showPassword.setImageResource(R.drawable.ic_show_pw);
                isPasswordVisible = true;
            }
            binding.password.setSelection(binding.password.getText().length());
        });

        binding.register.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        Eemail = binding.email;
        Epassword = binding.password;

        binding.sendOtp.setOnClickListener(v -> {
            String email = Eemail.getText().toString();
            String password = Epassword.getText().toString();

            if (TextUtils.isEmpty(email)) {
                Eemail.setError("Email wajib diisi");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Epassword.setError("Password wajib diisi");
                return;
            }

            login(email, password);
        });

    }

    private void login(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.login(request).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().get("message").getAsString();
                    Toast.makeText(LoginActivity.this, "Login berhasil: " + message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("from", "login");
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Login gagal: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
