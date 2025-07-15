package com.novdev.kasirku.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.novdev.kasirku.R;
import com.novdev.kasirku.databinding.SplashScreenBinding;

public class SplashScreenActivity extends AppCompatActivity {
    SplashScreenBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash_video);
        binding.videoView.setVideoURI(video);

        binding.videoView.setOnCompletionListener(mp -> {
            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            finish();
        });

        binding.videoView.start();
    }
}
