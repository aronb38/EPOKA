package com.example.epoka;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 4000; // Durée de l'écran de lancement en millisecondes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView ivSplash = findViewById(R.id.iv_splash);
        Glide.with(this).asGif().load(R.drawable.epoka).into(ivSplash);

        // Démarrer l'activité principale après la durée spécifiée

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}