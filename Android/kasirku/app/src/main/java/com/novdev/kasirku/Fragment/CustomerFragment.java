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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.novdev.kasirku.Activity.CashierActivity;
import com.novdev.kasirku.Activity.MainActivity;
import com.novdev.kasirku.Adapter.CustomerAdapter;
import com.novdev.kasirku.Adapter.ProductAdapter;
import com.novdev.kasirku.DB.TokenDBHelper;
import com.novdev.kasirku.Model.Customer;
import com.novdev.kasirku.Model.Product;
import com.novdev.kasirku.Model.TokenModel;
import com.novdev.kasirku.R;
import com.novdev.kasirku.Retrofit.ApiClient;
import com.novdev.kasirku.Retrofit.ApiService;
import com.novdev.kasirku.databinding.FragmentCustomerBinding;
import com.novdev.kasirku.databinding.FragmentProductBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerFragment extends Fragment {
    FragmentCustomerBinding binding;
    private CustomerAdapter adapter;
    private List<Customer> customerList = new ArrayList<>();
    private TokenDBHelper dbHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // View Binding untuk inflate layout
        binding = FragmentCustomerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = new TokenDBHelper(getContext());
        binding.addCustomer.setOnClickListener(v -> {
            replaceFragmentAddCustomer();
        });

        setUpRecyclerView();
        loadCustomers();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setToolbarTitle("Kelola Pelanggan");
    }

    private void setUpRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CustomerAdapter(getContext(), customerList);
        binding.recyclerView.setAdapter(adapter);
    }


    private void loadCustomers() {
        TokenModel tokenModel = dbHelper.getToken();
        String token = "Bearer " + tokenModel.getToken();
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getCustomerPurchases(token).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonArray arr = response.body().getAsJsonArray("data");
                    customerList.clear();
                    for (JsonElement el : arr) {
                        JsonObject obj = el.getAsJsonObject();
                        int id = obj.get("id").getAsInt();
                        String name = obj.get("name").getAsString();
                        int total = obj.get("total_purchases").getAsInt();

                        customerList.add(new Customer(id, name, total));
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Gagal ambil data", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "onResponse: " + response.body());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // hindari memory leak
    }

    private void replaceFragmentAddCustomer() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new CustomerAddFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
