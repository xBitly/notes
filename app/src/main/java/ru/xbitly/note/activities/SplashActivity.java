package ru.xbitly.note.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SharedPreferences settings = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean night_mode = settings.getBoolean("night_mode", false);
        boolean first_start = settings.getBoolean("first_start", true);
        if(night_mode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);

        Intent intent;
        if(first_start) {
            intent = new Intent(SplashActivity.this, HelloActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();
    }
}