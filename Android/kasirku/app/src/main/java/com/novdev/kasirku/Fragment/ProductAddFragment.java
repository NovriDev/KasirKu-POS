package com.novdev.kasirku.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonObject;
import com.novdev.kasirku.Activity.CashierActivity;
import com.novdev.kasirku.Activity.MainActivity;
import com.novdev.kasirku.Adapter.ProductAdapter;
import com.novdev.kasirku.DB.TokenDBHelper;
import com.novdev.kasirku.Model.Product;
import com.novdev.kasirku.Model.TokenModel;
import com.novdev.kasirku.R;
import com.novdev.kasirku.Retrofit.ApiClient;
import com.novdev.kasirku.Retrofit.ApiService;
import com.novdev.kasirku.Utils.FileUtils;
import com.novdev.kasirku.databinding.FragmentAddProductBinding;
import com.novdev.kasirku.databinding.FragmentProductBinding;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAddFragment extends Fragment {
    FragmentAddProductBinding binding;
    private Uri imageUri = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // View Binding untuk inflate layout
        binding = FragmentAddProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.imgProduct.setOnClickListener(v -> pickImage());
        setupPriceFormat();

        binding.btnSave.setOnClickListener(v -> saveProduct());
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setToolbarTitle("Dashboard");
    }

    private void setupPriceFormat() {
        binding.edtPrice.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    binding.edtPrice.removeTextChangedListener(this);
                    String cleanString = s.toString().replaceAll("[Rp,.\\s]", "");
                    if (!cleanString.isEmpty()) {
                        double parsed = Double.parseDouble(cleanString);
                        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
                        current = format.format(parsed).replace("Rp", "Rp ");
                        binding.edtPrice.setText(current);
                        binding.edtPrice.setSelection(current.length());
                    } else {
                        current = "";
                    }
                    binding.edtPrice.addTextChangedListener(this);
                }
            }
        });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    binding.imgProduct.setImageURI(imageUri);
                }
            });

    private void saveProduct() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Menyimpan produk...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String name = binding.edtName.getText().toString().trim();
        String sku = binding.edtSKU.getText().toString().trim();
        String price = binding.edtPrice.getText().toString().replaceAll("[Rp,.\\s]", "");
        String stockInitial = binding.edtStockInitial.getText().toString().trim();
        String stockCurrent = binding.edtStockCurrent.getText().toString().trim();

        if (name.isEmpty() || sku.isEmpty() || price.isEmpty() || stockInitial.isEmpty() || stockCurrent.isEmpty()) {
            Toast.makeText(getContext(), "Semua field wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        MultipartBody.Part imagePart = null;
        if (imageUri != null) {
            File file = new File(FileUtils.getPath(getContext(), imageUri));
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            imagePart = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.createProduct(
                "Bearer " + new TokenDBHelper(getContext()).getToken().getToken(),
                imagePart,
                RequestBody.create(MediaType.parse("text/plain"), name),
                RequestBody.create(MediaType.parse("text/plain"), sku),
                RequestBody.create(MediaType.parse("text/plain"), price),
                RequestBody.create(MediaType.parse("text/plain"), stockInitial),
                RequestBody.create(MediaType.parse("text/plain"), stockCurrent)
        ).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Produk berhasil disimpan", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Gagal simpan produk", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
