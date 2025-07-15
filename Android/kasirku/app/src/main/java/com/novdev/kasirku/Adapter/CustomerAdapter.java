package com.novdev.kasirku.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.novdev.kasirku.Model.Customer;
import com.novdev.kasirku.Model.Product;
import com.novdev.kasirku.R;
import com.novdev.kasirku.Utils.FormatRupiah;
import com.novdev.kasirku.Utils.TextDrawableUtil;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {
    private List<Customer> customerList;
    private Context context;

    public CustomerAdapter(Context ctx, List<Customer> customers) {
        this.context = ctx;
        this.customerList = customers;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer c = customerList.get(position);
        holder.txtName.setText(c.getName());
        holder.txtCustomer.setText("Pembelian ke-" + c.getTotalPurchases());
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtCustomer;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtProductName);
            txtCustomer = itemView.findViewById(R.id.txtCustomer);
        }
    }
}
