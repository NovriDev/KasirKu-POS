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
import com.novdev.kasirku.R;
import com.novdev.kasirku.Utils.FormatRupiah;
import com.novdev.kasirku.Utils.TextDrawableUtil;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {
    private List<Product> productList;
    private Context context;

    public StockAdapter(Context ctx, List<Product> products) {
        this.context = ctx;
        this.productList = products;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        Product p = productList.get(position);
        holder.txtName.setText(p.getName());
        holder.txtPrice.setText("Harga : " + FormatRupiah.formatRupiah(p.getPrice()));
        holder.txtStock.setText("Stok : " + p.getStock_current());

        if (p.getImageUrl() != null) {
            Glide.with(context)
                    .load(p.getImageUrl())
                    .into(holder.imgProduct);
        } else {
            Bitmap avatar = TextDrawableUtil.createCircleAvatar(context, p.getName(), Color.GRAY, 100);
            holder.imgProduct.setImageBitmap(avatar);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class StockViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtName, txtPrice, txtStock;

        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtName = itemView.findViewById(R.id.txtProductName);
            txtPrice = itemView.findViewById(R.id.txtProductPrice);
            txtStock = itemView.findViewById(R.id.txtStock);
        }
    }
}
