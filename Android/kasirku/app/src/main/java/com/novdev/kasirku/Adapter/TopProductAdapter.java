package com.novdev.kasirku.Adapter;

import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.novdev.kasirku.Model.TopProduct;
import com.novdev.kasirku.R;

import java.util.List;

public class TopProductAdapter extends RecyclerView.Adapter<TopProductAdapter.ViewHolder> {
    private List<TopProduct> products;
    private int maxSold;

    public TopProductAdapter(List<TopProduct> products) {
        this.products = products;
        this.maxSold = products.isEmpty() ? 1 : products.get(0).getTotalSold(); // assuming sorted desc
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TopProduct product = products.get(position);
        holder.productName.setText((position + 1) + ". " + product.getName());
        holder.productSold.setText(product.getTotalSold() + " pcs");

        int progress = (int) ((product.getTotalSold() * 100.0) / maxSold);
        holder.productProgress.setProgress(0);
        animateProgressBar(holder.productProgress, progress);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    private void animateProgressBar(ProgressBar progressBar, int targetProgress) {
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, targetProgress);
        animation.setDuration(800);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productSold;
        ProgressBar productProgress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productSold = itemView.findViewById(R.id.productSold);
            productProgress = itemView.findViewById(R.id.productProgress);
        }
    }
}
