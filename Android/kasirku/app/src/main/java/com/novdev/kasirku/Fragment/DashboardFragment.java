package com.novdev.kasirku.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.JsonObject;
import com.novdev.kasirku.Activity.CashierActivity;
import com.novdev.kasirku.Activity.MainActivity;
import com.novdev.kasirku.DB.TokenDBHelper;
import com.novdev.kasirku.R;
import com.novdev.kasirku.Retrofit.ApiClient;
import com.novdev.kasirku.Retrofit.ApiService;
import com.novdev.kasirku.Utils.FormatRupiah;
import com.novdev.kasirku.databinding.FragmentDashboardBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // View Binding untuk inflate layout
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.product.setOnClickListener(v -> {
            replaceFragmentProduct();
        });
        binding.stock.setOnClickListener(v -> {
            replaceFragmentStock();
        });
        binding.cashierMode.setOnClickListener(v -> {
            replaceFragmentTransaction();
        });
        binding.history.setOnClickListener(v -> {
            replaceFragmentHistory();
        });
        binding.customer.setOnClickListener(v -> {
            replaceFragmentCustomer();
        });
        binding.report.setOnClickListener(v -> {
            replaceFragmentReport();
        });

        setupTabListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setToolbarTitle("Dashboard");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // hindari memory leak
    }

    private void setupTabListeners() {
        binding.tabDay.setOnClickListener(v -> {
            resetTabBackground();
            binding.tabDay.setBackgroundColor(Color.parseColor("#0D00E2"));
            loadSummary("day");
        });

        binding.tabWeek.setOnClickListener(v -> {
            resetTabBackground();
            binding.tabWeek.setBackgroundColor(Color.parseColor("#0D00E2"));
            loadSummary("week");
        });

        binding.tabMonth.setOnClickListener(v -> {
            resetTabBackground();
            binding.tabMonth.setBackgroundColor(Color.parseColor("#0D00E2"));
            loadSummary("month");
        });

    }

    private void resetTabBackground() {
        binding.tabDay.setBackgroundResource(R.drawable.rounded_square_left);
        binding.tabWeek.setBackgroundColor(Color.parseColor("#827BF9"));
        binding.tabMonth.setBackgroundResource(R.drawable.rounded_square_right);
    }


    private void replaceFragmentProduct() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new ProductFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void replaceFragmentStock() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new StockFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void replaceFragmentHistory() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new HistoryFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void replaceFragmentTransaction() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new TransactionProductFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void replaceFragmentCustomer() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new CustomerFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void replaceFragmentReport() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new ReportFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadSummary(String filter) {
        String token = "Bearer " + new TokenDBHelper(getContext()).getToken().getToken();
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getDashboardSummary(filter, "Bearer " + token).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject obj = response.body();
                    binding.countSelling.setText(obj.get("total_sales").getAsString());
                    binding.activeCostumer.setText(obj.get("active_customers").getAsString());
                    binding.income.setText(FormatRupiah.formatRupiah(obj.get("income").getAsDouble()));
                } else {
                    Toast.makeText(getContext(), "Data gagal dimuat", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Gagal terhubung ke server", Toast.LENGTH_SHORT).show();
            }
        });
    }

}