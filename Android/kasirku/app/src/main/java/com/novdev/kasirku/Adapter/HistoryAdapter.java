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
import com.novdev.kasirku.Model.Product;
import com.novdev.kasirku.Model.PurchaseItemModel;
import com.novdev.kasirku.R;
import com.novdev.kasirku.Utils.FormatRupiah;
import com.novdev.kasirku.Utils.TextDrawableUtil;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<PurchaseItemModel> purchaseList;
    private Context context;

    public HistoryAdapter(Context ctx, List<PurchaseItemModel> purchases) {
        this.context = ctx;
        this.purchaseList = purchases;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        PurchaseItemModel p = purchaseList.get(position);
        holder.txtName.setText(p.getProduct_name());
        holder.txtPrice.setText("Harga : " + FormatRupiah.formatRupiah(p.getSubtotal()));
        holder.txtStock.setText("Stok : " + p.getQuantity());

        if (p.getCustomer_name() != null){
            holder.txtCustomer.setText("Pembeli : " + p.getCustomer_name());
        } else {
            holder.txtCustomer.setText("Pembeli : Umum");
        }

    }

    @Override
    public int getItemCount() {
        return purchaseList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPrice, txtStock, txtCustomer;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtProductName);
            txtPrice = itemView.findViewById(R.id.txtProductPrice);
            txtStock = itemView.findViewById(R.id.txtStock);
            txtCustomer = itemView.findViewById(R.id.txtCustomer);
        }
    }
}
