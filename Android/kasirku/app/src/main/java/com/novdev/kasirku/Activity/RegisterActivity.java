package com.novdev.kasirku.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.novdev.kasirku.DB.TokenDBHelper;
import com.novdev.kasirku.R;
import com.novdev.kasirku.Request.LoginRequest;
import com.novdev.kasirku.Request.RegisterRequest;
import com.novdev.kasirku.Retrofit.ApiClient;
import com.novdev.kasirku.Retrofit.ApiService;
import com.novdev.kasirku.databinding.ActivityLoginBinding;
import com.novdev.kasirku.databinding.ActivityRegisterBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    EditText Ename, Eemail, Epassword, EconfirmPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());

        binding.showPassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                binding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.showPassword.setImageResource(R.drawable.ic_not_show_pw);
                isPasswordVisible = false;
            } else {
                binding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                binding.showPassword.setImageResource(R.drawable.ic_show_pw);
                isPasswordVisible = true;
            }
            binding.password.setSelection(binding.password.getText().length());
        });

        binding.showConfirmPassword.setOnClickListener(v -> {
            if (isConfirmPasswordVisible) {
                binding.confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.showConfirmPassword.setImageResource(R.drawable.ic_not_show_pw);
                isConfirmPasswordVisible = false;
            } else {
                binding.confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                binding.showConfirmPassword.setImageResource(R.drawable.ic_show_pw);
                isConfirmPasswordVisible = true;
            }
            binding.confirmPassword.setSelection(binding.confirmPassword.getText().length());
        });


        binding.login.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        Eemail = binding.email;
        Ename = binding.name;
        Epassword = binding.password;
        EconfirmPassword = binding.confirmPassword;

        binding.sendOtp.setOnClickListener(v -> {
            String email = binding.email.getText().toString();
            String name = binding.name.getText().toString();
            String password = binding.password.getText().toString();
            String confirmPassword = binding.confirmPassword.getText().toString();

            if (TextUtils.isEmpty(email)) {
                binding.email.setError("Email wajib diisi");
                return;
            }
            if (TextUtils.isEmpty(name)) {
                binding.name.setError("Nama wajib diisi");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                binding.password.setError("Password wajib diisi");
                return;
            }
            if (password.length() < 8) {
                binding.password.setError("Password minimal 8 karakter");
                return;
            }
            if (!password.equals(confirmPassword)) {
                binding.confirmPassword.setError("Konfirmasi password tidak cocok");
                return;
            }

            register(email, name, password, confirmPassword);
        });

    }

    private void register(String email, String name, String password, String confirmPassword) {
        RegisterRequest request = new RegisterRequest(email, name, password, confirmPassword);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.register(request).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().get("message").getAsString();
                    Toast.makeText(RegisterActivity.this, "Register berhasil: " + message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, OtpActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("from", "register");
                    startActivity(intent);
                } else {
                    Toast.makeText(RegisterActivity.this, "Register gagal: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
