package com.novdev.kasirku.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.novdev.kasirku.DB.TokenDBHelper;
import com.novdev.kasirku.Fragment.ProductEditFragment;
import com.novdev.kasirku.Model.Product;
import com.novdev.kasirku.R;
import com.novdev.kasirku.Retrofit.ApiClient;
import com.novdev.kasirku.Retrofit.ApiService;
import com.novdev.kasirku.Utils.FormatRupiah;
import com.novdev.kasirku.Utils.TextDrawableUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private Context context;
    private OnItemClickListener listener;

    public ProductAdapter(Context ctx, List<Product> products) {
        this.context = ctx;
        this.productList = products;
    }

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }


    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
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
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(p);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            ProductEditFragment editFragment = new ProductEditFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("product_id", p.getId());
            editFragment.setArguments(bundle);

            ((AppCompatActivity) v.getContext())
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, editFragment)
                    .addToBackStack(null)
                    .commit();
        });

        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Hapus Produk")
                    .setMessage("Yakin ingin menghapus produk ini?")
                    .setPositiveButton("Hapus", (dialog, which) -> {
                        deleteProduct(p, position);
                    })
                    .setNegativeButton("Batal", null)
                    .show();
            return true;
        });
    }

    private void deleteProduct(Product product, int position) {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        String token = "Bearer " + new TokenDBHelper(context).getToken().getToken();

        api.deleteProduct(token, product.getId()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    productList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Produk berhasil dihapus", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Gagal menghapus produk", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtName, txtPrice, txtStock;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtName = itemView.findViewById(R.id.txtProductName);
            txtPrice = itemView.findViewById(R.id.txtProductPrice);
            txtStock = itemView.findViewById(R.id.txtStock);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
