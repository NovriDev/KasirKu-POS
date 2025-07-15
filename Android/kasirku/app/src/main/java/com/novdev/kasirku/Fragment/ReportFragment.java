package com.novdev.kasirku.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.novdev.kasirku.Activity.CashierActivity;
import com.novdev.kasirku.Activity.MainActivity;
import com.novdev.kasirku.Adapter.HistoryAdapter;
import com.novdev.kasirku.Adapter.TopProductAdapter;
import com.novdev.kasirku.DB.TokenDBHelper;
import com.novdev.kasirku.Model.PurchaseItemModel;
import com.novdev.kasirku.Model.TokenModel;
import com.novdev.kasirku.Model.TopProduct;
import com.novdev.kasirku.Response.TopProductResponse;
import com.novdev.kasirku.Retrofit.ApiClient;
import com.novdev.kasirku.Retrofit.ApiService;
import com.novdev.kasirku.databinding.FragmentStockBinding;
import com.novdev.kasirku.databinding.FragmentTopProductBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportFragment extends Fragment {
    private FragmentTopProductBinding binding;
    private TopProductAdapter adapter;
    private List<TopProduct> productList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTopProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpRecyclerView();
        fetchTopProducts();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setToolbarTitle("Laporan Penjualan");
    }

    private void setUpRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TopProductAdapter(productList);
        binding.recyclerView.setAdapter(adapter);
    }

    private void fetchTopProducts() {
        String token = new TokenDBHelper(requireContext()).getToken().getToken();
        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.getTopProducts("Bearer " + token).enqueue(new Callback<TopProductResponse>() {
            @Override
            public void onResponse(Call<TopProductResponse> call, Response<TopProductResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    productList.clear();
                    productList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Data kosong atau gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TopProductResponse> call, Throwable t) {
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
