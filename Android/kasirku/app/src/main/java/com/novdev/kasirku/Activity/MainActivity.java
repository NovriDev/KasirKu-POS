package com.novdev.kasirku.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.novdev.kasirku.DB.TokenDBHelper;
import com.novdev.kasirku.Fragment.DashboardFragment;
import com.novdev.kasirku.Model.TokenModel;
import com.novdev.kasirku.R;
import com.novdev.kasirku.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    DrawerLayout drawerLayout;
    TokenDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#5852CC"));
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dbHelper = new TokenDBHelper(this);
        TokenModel tokenModel = dbHelper.getToken();

        drawerLayout = binding.drawerLayout;
        binding.drawerAct.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        if (tokenModel == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        long now = System.currentTimeMillis();
        long expiredLimit = 90L * 24 * 60 * 60 * 1000;

        if (now - tokenModel.getCreatedAt() > expiredLimit) {
            // Token expired, hapus dan redirect
            dbHelper.deleteToken();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        manageFragment();
    }

    private void manageFragment(){
        binding.title.setText("Dashboard");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new DashboardFragment())
                .commit();
    }

    public void setToolbarTitle(String title) {
        binding.title.setText(title);
    }
}