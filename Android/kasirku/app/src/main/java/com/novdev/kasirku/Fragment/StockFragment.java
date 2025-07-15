package com.novdev.kasirku.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.novdev.kasirku.Activity.CashierActivity;
import com.novdev.kasirku.Activity.MainActivity;
import com.novdev.kasirku.Adapter.ProductAdapter;
import com.novdev.kasirku.DB.TokenDBHelper;
import com.novdev.kasirku.Model.Product;
import com.novdev.kasirku.Model.PurchaseItemModel;
import com.novdev.kasirku.Model.TokenModel;
import com.novdev.kasirku.Retrofit.ApiClient;
import com.novdev.kasirku.Retrofit.ApiService;
import com.novdev.kasirku.databinding.FragmentProductBinding;
import com.novdev.kasirku.databinding.FragmentStockBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockFragment extends Fragment {
    FragmentStockBinding binding;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private TokenDBHelper dbHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // View Binding untuk inflate layout
        binding = FragmentStockBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = new TokenDBHelper(getContext());
        binding.recyclerView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CashierActivity.class);
            startActivity(intent);
        });

        setUpRecyclerView();
        loadProducts();

        binding.exportExcel.setOnClickListener(v -> {
            exportStockToCSV(productList);

            File file = new File(getContext().getExternalFilesDir(null), "data_stok.csv");
            Uri uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "text/csv");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), "Tidak ada aplikasi untuk membuka file CSV", Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setToolbarTitle("Kelola Stok");
    }

    private void setUpRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductAdapter(getContext(), productList);
        binding.recyclerView.setAdapter(adapter);
    }

    private void loadProducts() {
        TokenModel tokenModel = dbHelper.getToken();
        String token = "Bearer " + tokenModel.getToken();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getProducts(token).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Gagal ambil data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void exportStockToCSV(List<Product> productList) {
        StringBuilder data = new StringBuilder();
        data.append("ID,,Nama Barang,,Kode Barang,,Stok Awal,,Stok Sekarang,,Harga\n");

        for (Product product : productList) {
            data.append(product.getId()).append(",")  // Nama Barang
                    .append(",")                                     // Spacer
                    .append(product.getName()).append(",")       // Qty
                    .append(",")                                     // Spacer
                    .append(product.getSku()).append(",")       // Total Harga
                    .append(",")                                     // Spacer
                    .append(product.getStock_initial()).append(",")
                    .append(",")
                    .append(product.getStock_current()).append(",")
                    .append(",")
                    .append(product.getPrice()).append(",")
                    .append("\n");
        }


        try {
            File file = new File(getContext().getExternalFilesDir(null), "data_stok.csv");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data.toString().getBytes());
            fos.close();
            Toast.makeText(getContext(), "CSV berhasil disimpan di:\n" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Gagal menyimpan file", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // hindari memory leak
    }
}
