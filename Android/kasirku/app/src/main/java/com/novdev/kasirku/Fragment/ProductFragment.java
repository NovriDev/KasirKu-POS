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
import androidx.recyclerview.widget.RecyclerView;

import com.novdev.kasirku.Activity.CashierActivity;
import com.novdev.kasirku.Activity.MainActivity;
import com.novdev.kasirku.Adapter.ProductAdapter;
import com.novdev.kasirku.DB.TokenDBHelper;
import com.novdev.kasirku.Model.Product;
import com.novdev.kasirku.Model.TokenModel;
import com.novdev.kasirku.R;
import com.novdev.kasirku.Retrofit.ApiClient;
import com.novdev.kasirku.Retrofit.ApiService;
import com.novdev.kasirku.databinding.FragmentDashboardBinding;
import com.novdev.kasirku.databinding.FragmentProductBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFragment extends Fragment {
    FragmentProductBinding binding;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private TokenDBHelper dbHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // View Binding untuk inflate layout
        binding = FragmentProductBinding.inflate(inflater, container, false);
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
        binding.addProduct.setOnClickListener(v -> {
            replaceFragmentAddProduct();
        });

        setUpRecyclerView();
        loadProducts();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setToolbarTitle("Kelola Produk");
    }

    private void setUpRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductAdapter(getContext(), productList);
        binding.recyclerView.setAdapter(adapter);

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
                    Log.d("TAG", "onResponse: " + response.body());
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
        binding = null; // hindari memory leak
    }

    private void replaceFragmentAddProduct() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new ProductAddFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
