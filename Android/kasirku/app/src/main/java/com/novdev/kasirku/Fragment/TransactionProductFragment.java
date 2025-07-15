package com.novdev.kasirku.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.novdev.kasirku.Activity.CashierActivity;
import com.novdev.kasirku.Activity.MainActivity;
import com.novdev.kasirku.Adapter.ProductAdapter;
import com.novdev.kasirku.Adapter.TransactionProductAdapter;
import com.novdev.kasirku.DB.TokenDBHelper;
import com.novdev.kasirku.Model.Product;
import com.novdev.kasirku.Model.TokenModel;
import com.novdev.kasirku.R;
import com.novdev.kasirku.Retrofit.ApiClient;
import com.novdev.kasirku.Retrofit.ApiService;
import com.novdev.kasirku.Utils.FormatRupiah;
import com.novdev.kasirku.databinding.FragmentProductBinding;
import com.novdev.kasirku.databinding.FragmentTransactionBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionProductFragment extends Fragment {
    private FragmentTransactionBinding binding;
    private TransactionProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private List<Product> selectedProductsList = new ArrayList<>();
    private Map<Integer, Integer> selectedProductsMap = new HashMap<>();
    private TokenDBHelper dbHelper;
    private int totalItems = 0;
    private int totalPrice = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTransactionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = new TokenDBHelper(getContext());

        setUpRecyclerView();
        loadProducts();

        binding.next.setOnClickListener(v -> {
            if (selectedProductsList.isEmpty()) {
                Toast.makeText(getContext(), "Pilih produk terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("SelectedProductsBeforeIntent", "Total Selected: " + adapter.getSelectedProductsList().size());
            for (Product p : adapter.getSelectedProductsList()) {
                Log.d("SelectedProductsBeforeIntent", "Product ID: " + p.getId() + " | Name: " + p.getName() + " | Qty: " + p.getStockCount());
            }

            Intent intent = new Intent(getContext(), CashierActivity.class);
            intent.putExtra("total_items", totalItems);
            intent.putExtra("total_price", totalPrice);
            intent.putExtra("selected_products_list", new ArrayList<>(adapter.getSelectedProductsList()));
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setToolbarTitle("Transaksi");
    }

    private void setUpRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionProductAdapter(productList, (items, price, selectedList, selectedMap) -> {
            totalItems = items;
            totalPrice = price;
            this.selectedProductsList = selectedList;
            this.selectedProductsMap = selectedMap;
            binding.countProduk.setText(items + " Barang");
            binding.total.setText("Total: Rp " + FormatRupiah.formatRupiah(price));
        });
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
                    Toast.makeText(getContext(), "Gagal mengambil data produk", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
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
