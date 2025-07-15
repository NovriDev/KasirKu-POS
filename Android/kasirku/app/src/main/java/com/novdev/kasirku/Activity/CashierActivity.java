package com.novdev.kasirku.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.novdev.kasirku.DB.TokenDBHelper;
import com.novdev.kasirku.Model.Customer;
import com.novdev.kasirku.Model.Product;
import com.novdev.kasirku.R;
import com.novdev.kasirku.Response.CustomerResponse;
import com.novdev.kasirku.Retrofit.ApiClient;
import com.novdev.kasirku.Retrofit.ApiService;
import com.novdev.kasirku.Utils.FormatRupiah;
import com.novdev.kasirku.databinding.ActivityCashierBinding;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CashierActivity extends AppCompatActivity {
    private ActivityCashierBinding binding;
    private String currentInput = "0";
    private int selectedCustomerId = 0;
    private String selectedCustomerName = "Umum";
    private String selectedPaymentMethod = "Tunai";
    private int totalPriceAfterDiscount = 0;
    private long inputAmount = 0;
    private int totalPrice;
    private int totalItems;
    private List<Product> selectedProducts = new ArrayList<>();
    private List<Customer> customerList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCashierBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(Color.parseColor("#009DFF"));

        totalPrice = getIntent().getIntExtra("total_price", 0);
        totalPriceAfterDiscount = totalPrice;
        totalItems = getIntent().getIntExtra("total_items", 0);
        selectedProducts = (ArrayList<Product>) getIntent().getSerializableExtra("selected_products_list");

        if (selectedProducts != null) {
            Log.d("CashierIntent", "Selected Products Size: " + selectedProducts.size());
            for (Product p : selectedProducts) {
                Log.d("CashierIntent", "Product ID: " + p.getId() + " | Name: " + p.getName() + " | Qty: " + p.getStockCount());
            }
        } else {
            Log.d("CashierIntent", "Selected Products is NULL");
        }

        binding.paymentTotal.setText(FormatRupiah.formatRupiah(totalPrice));

        setNumberPadListeners();
        updateInputDisplay();
        fetchCustomers();

        binding.chooseCustomer.setOnClickListener(v -> {
            if (customerList.isEmpty()) {
                Toast.makeText(this, "Data customer kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] customerNames = new String[customerList.size()];
            for (int i = 0; i < customerList.size(); i++) {
                customerNames[i] = customerList.get(i).getName();
            }

            new AlertDialog.Builder(this)
                    .setTitle("Pilih Pelanggan")
                    .setItems(customerNames, (dialog, which) -> {
                        Customer selected = customerList.get(which);
                        binding.chooseCustomer.setText("Pelanggan : " + selected.getName());
                        selectedCustomerName = selected.getName();
                        // Simpan ID kalau perlu di submit
                    }).show();
        });


        binding.chooseMethod.setOnClickListener(v -> {
            String[] methods = {"Tunai"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Pilih Metode Bayar");
            builder.setItems(methods, (dialog, which) -> {
                binding.chooseMethod.setText("Metode : " + methods[which]);
                selectedPaymentMethod = methods[which];
            });
            builder.show();
        });

        binding.discount.setOnClickListener(v -> {
            EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);

            new AlertDialog.Builder(this)
                    .setTitle("Masukkan Diskon (%)")
                    .setView(input)
                    .setPositiveButton("OK", (dialog, which) -> {
                        String persen = input.getText().toString();
                        if (!persen.isEmpty()) {
                            int discountPercent = Integer.parseInt(persen);
                            binding.discount.setText("Diskon : " + discountPercent + "%");

                            // Hitung total setelah diskon
                            int totalAfterDiscount = totalPrice - (totalPrice * discountPercent / 100);
                            binding.paymentTotal.setText(FormatRupiah.formatRupiah(totalAfterDiscount));}
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });


        binding.submit.setOnClickListener(v -> {
            submitTransaction();
        });
    }

    private void setNumberPadListeners() {
        binding.num0.setOnClickListener(v -> appendNumber("0"));
        binding.num1.setOnClickListener(v -> appendNumber("1"));
        binding.num2.setOnClickListener(v -> appendNumber("2"));
        binding.num3.setOnClickListener(v -> appendNumber("3"));
        binding.num4.setOnClickListener(v -> appendNumber("4"));
        binding.num5.setOnClickListener(v -> appendNumber("5"));
        binding.num6.setOnClickListener(v -> appendNumber("6"));
        binding.num7.setOnClickListener(v -> appendNumber("7"));
        binding.num8.setOnClickListener(v -> appendNumber("8"));
        binding.num9.setOnClickListener(v -> appendNumber("9"));
        binding.num00.setOnClickListener(v -> appendNumber("00"));
        binding.num000.setOnClickListener(v -> appendNumber("000"));

        binding.clearInput.setOnClickListener(v -> {
            currentInput = "0";
            updateInputDisplay();
        });

        binding.backSpace.setOnClickListener(v -> {
            if (currentInput.length() > 1) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
            } else {
                currentInput = "0";
            }
            updateInputDisplay();
        });
    }

    private void appendNumber(String number) {
        if (currentInput.equals("0")) {
            currentInput = number;
        } else {
            currentInput += number;
        }
        updateInputDisplay();
    }

    private void updateInputDisplay() {
        long amount;
        try {
            amount = Long.parseLong(currentInput.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            amount = 0;
            currentInput = "0";
        }
        this.inputAmount = amount;

        String formatted = "Rp" + NumberFormat.getInstance(new Locale("in", "ID")).format(amount);
        binding.inputPayment.setText(formatted);

        Log.d("CASHIER_INPUT", "Input: " + currentInput + " | Formatted: " + formatted + " | Amount: " + amount);

        validatePaymentInput(amount);
    }

    private void validatePaymentInput(long inputAmount) {
        Log.d("CASHIER_VALIDATE", "InputAmount: " + inputAmount + " | TotalPrice: " + totalPrice);

        if (inputAmount >= totalPrice) {
            binding.submit.setEnabled(true);
            binding.submit.setAlpha(1f);
            Log.d("CASHIER_VALIDATE", "Button ENABLED");
        } else {
            binding.submit.setEnabled(false);
            binding.submit.setAlpha(0.5f);
            binding.submit.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#535A5E")));
            Log.d("CASHIER_VALIDATE", "Button DISABLED");
        }
    }

    private void fetchCustomers() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Memuat pelanggan...");
        dialog.setCancelable(false);
        dialog.show();

        String token = new TokenDBHelper(this).getToken().getToken();
        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.getCustomers("Bearer " + token).enqueue(new Callback<CustomerResponse>() {
            @Override
            public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    List<Customer> customers = response.body().getData();
                    customerList.clear();
                    customerList.addAll(customers);

                    for (Customer c : customers) {
                        Log.d("FETCH_CUSTOMERS", "ID=" + c.getId() + ", Name=" + c.getName());
                    }
                } else {
                    Toast.makeText(CashierActivity.this, "Gagal memuat pelanggan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CustomerResponse> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(CashierActivity.this, "Gagal memuat pelanggan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitHistory() {

        Log.d("CashierIntent", "Total Items: " + totalItems + ", Total Price: " + totalPrice);
        Log.d("CashierIntent", "Selected Products: " + selectedProducts.size());
        for (Product p : selectedProducts) {
            Log.d("CashierIntent", "Product: " + p.getName() + " Qty: " + p.getStockCount());
        }

        String token = new TokenDBHelper(this).getToken().getToken();
        ApiService api = ApiClient.getClient().create(ApiService.class);

        Map<String, Object> payload = new HashMap<>();
        payload.put("customer_id", selectedCustomerId == 0 ? null : selectedCustomerId);
        payload.put("total_amount", totalPriceAfterDiscount);
        payload.put("payment_method", selectedPaymentMethod);

        List<Map<String, Object>> items = new ArrayList<>();
        Log.d("SUBMIT_DEBUG", "=== Produk yang dikirim ===");
        for (Product p : selectedProducts) {
            Log.d("SUBMIT_DEBUG", "ID: " + p.getId() + " | Qty: " + p.getStockCount() + " | Subtotal: " + (p.getPrice() * p.getStockCount()));
            Map<String, Object> item = new HashMap<>();
            item.put("product_id", p.getId());
            item.put("quantity", p.getStockCount());
            item.put("subtotal", p.getPrice() * p.getStockCount());
            items.add(item);
        }
        payload.put("items", items);

        Log.d("SUBMIT_DEBUG", "Payload: " + new Gson().toJson(payload));

        api.postPurchaseHistory("Bearer " + token, payload).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.d("SUBMIT_DEBUG", "Response sukses: " + response.body());
                    Toast.makeText(CashierActivity.this, "Transaksi disimpan di history !", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("SUBMIT_DEBUG", "Gagal: " + response.code() + " | " + response.message());
                    Toast.makeText(CashierActivity.this, "Gagal: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("SUBMIT_DEBUG", "Failure: " + t.getMessage());
                Toast.makeText(CashierActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void submitTransaction() {

        Log.d("CashierIntent", "Total Items: " + totalItems + ", Total Price: " + totalPrice);
        Log.d("CashierIntent", "Selected Products: " + selectedProducts.size());

        for (Product p : selectedProducts) {
            Log.d("CashierIntent", "Product: " + p.getName() + " Qty: " + p.getStockCount());
        }

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Memproses transaksi...");
        dialog.setCancelable(false);
        dialog.show();

        String token = new TokenDBHelper(this).getToken().getToken();
        ApiService api = ApiClient.getClient().create(ApiService.class);

        Map<String, Object> payload = new HashMap<>();
        payload.put("customer_id", selectedCustomerId == 0 ? null : selectedCustomerId);
        payload.put("total_amount", totalPriceAfterDiscount == 0 ? totalPrice : totalPriceAfterDiscount);
        payload.put("payment_method", selectedPaymentMethod);
        Log.d("SUBMIT_DEBUG", new Gson().toJson(payload));

        List<Map<String, Object>> items = new ArrayList<>();
        for (Product p : selectedProducts) {
            Map<String, Object> item = new HashMap<>();
            item.put("product_id", p.getId());
            item.put("quantity", p.getStockCount());
            item.put("subtotal", p.getPrice() * p.getStockCount());
            items.add(item);
        }
        payload.put("items", items);

        api.postPurchases("Bearer " + token, payload).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(CashierActivity.this, "Transaksi berhasil!", Toast.LENGTH_SHORT).show();
                    submitHistory();
                    Intent intent = new Intent(CashierActivity.this, InvoiceActivity.class);
                    intent.putExtra("total_price", totalPriceAfterDiscount == 0 ? totalPrice : totalPriceAfterDiscount);
                    intent.putExtra("payment_amount", inputAmount);
                    intent.putExtra("payment_method", selectedPaymentMethod);
                    intent.putExtra("customer_name", selectedCustomerName);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CashierActivity.this, "Gagal: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(CashierActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
