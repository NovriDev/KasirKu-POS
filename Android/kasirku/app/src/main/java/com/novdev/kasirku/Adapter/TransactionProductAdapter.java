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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.novdev.kasirku.Model.Product;
import com.novdev.kasirku.R;
import com.novdev.kasirku.Utils.FormatRupiah;
import com.novdev.kasirku.Utils.TextDrawableUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionProductAdapter extends RecyclerView.Adapter<TransactionProductAdapter.TransactionViewHolder> {
    private OnProductSelectedListener listener;
    private List<Product> productList;
    private Map<Integer, Integer> selectedProductsMap = new HashMap<>();

    public TransactionProductAdapter(List<Product> products, OnProductSelectedListener listener) {
        this.productList = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_product, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Product p = productList.get(position);
        holder.txtName.setText(p.getName());
        holder.txtPrice.setText("Harga : " + FormatRupiah.formatRupiah(p.getPrice()));
        holder.txtStock.setText("Stok : " + p.getStock_current());

        if (p.getImageUrl() != null) {
            Glide.with(holder.itemView.getContext()).load(p.getImageUrl()).into(holder.imgProduct);
        } else {
            Bitmap avatar = TextDrawableUtil.createCircleAvatar(holder.itemView.getContext(), p.getName(), Color.GRAY, 100);
            holder.imgProduct.setImageBitmap(avatar);
        }

        holder.txtStockPurchase.setText(String.valueOf(p.getStockCount()));

        if (p.getStockCount() > 0) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#D3D3D3"));
        } else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        holder.cardView.setOnClickListener(v -> {
            int currentCount = p.getStockCount();
            currentCount++;
            p.setStockCount(currentCount);
            selectedProductsMap.put(p.getId(), currentCount);
            notifyItemChanged(position);
            notifySelectionChanged();
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtName, txtPrice, txtStock, txtStockPurchase;
        CardView cardView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtName = itemView.findViewById(R.id.txtProductName);
            txtPrice = itemView.findViewById(R.id.txtProductPrice);
            txtStock = itemView.findViewById(R.id.txtStock);
            txtStockPurchase = itemView.findViewById(R.id.stockPurchase);
            cardView = itemView.findViewById(R.id.countBody);
        }
    }

    private void notifySelectionChanged() {
        List<Product> selectedList = new ArrayList<>();
        int totalItems = 0;
        int totalPrice = 0;

        for (Map.Entry<Integer, Integer> entry : selectedProductsMap.entrySet()) {
            int id = entry.getKey();
            int qty = entry.getValue();

            Product selected = findProductById(id);
            if (selected != null) {
                totalItems += qty;
                totalPrice += selected.getPrice() * qty;

                Product clone = new Product(selected.getId(), selected.getName(), selected.getPrice(), qty);
                selectedList.add(clone);
            }
        }

        if (listener != null) {
            listener.onProductSelected(totalItems, totalPrice, selectedList, selectedProductsMap);
        }
    }

    private Product findProductById(int id) {
        for (Product p : productList) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    public interface OnProductSelectedListener {
        void onProductSelected(int totalItems, int totalPrice, List<Product> selectedList, Map<Integer, Integer> selectedMap);
    }

    public List<Product> getSelectedProductsList() {
        List<Product> list = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : selectedProductsMap.entrySet()) {
            Product p = findProductById(entry.getKey());
            if (p != null) {
                Product clone = new Product(p.getId(), p.getName(), p.getPrice(), entry.getValue());
                list.add(clone);
            }
        }
        return list;
    }

    public Map<Integer, Integer> getSelectedProductsMap() {
        return selectedProductsMap;
    }
}
