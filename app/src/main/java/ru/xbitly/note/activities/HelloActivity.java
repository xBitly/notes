package ru.xbitly.note.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import ru.xbitly.note.R;

public class HelloActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SharedPreferences settings = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean night_mode = settings.getBoolean("night_mode", false);
        if(night_mode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        TextView textView = findViewById(R.id.text_hello);
        RelativeLayout buttonNext = findViewById(R.id.button_next);

        int timerFirst = 1000;
        String strFirst = getResources().getString(R.string.hello);
        int startIndexFirst = strFirst.indexOf(getResources().getString(R.string.app_name));
        int stopIndexFirst = startIndexFirst + getResources().getString(R.string.app_name).length();

        String strThird = getResources().getString(R.string.try_out);
        int startIndexThird = strThird.indexOf(getResources().getString(R.string.best_features));
        int stopIndexThird = startIndexThird + getResources().getString(R.string.best_features).length();


        for (char i : strFirst.toCharArray()) {
            new Handler().postDelayed(() -> {
                String text = textView.getText() + Character.toString(i);
                Spannable textSpan = new SpannableString(text);
                if(text.length() >= startIndexFirst && text.length() <= stopIndexFirst) textSpan.setSpan(new ForegroundColorSpan(getColor(R.color.red)), startIndexFirst, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                else if(text.length() > stopIndexFirst) textSpan.setSpan(new ForegroundColorSpan(getColor(R.color.red)), startIndexFirst, stopIndexFirst, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(textSpan);
                }, timerFirst);
            if(i == ' ') timerFirst += 200;
            else timerFirst += 100;
        }

        int timerSecond = 1000;
        for (int i = strFirst.length()-1; i >= 0; i--) {
            int finalI = i;
            new Handler().postDelayed(() -> {
                String text = textView.getText().toString().substring(0, finalI);
                textView.setText(text);
                }, timerSecond+timerFirst);
            timerSecond += 75;
        }

        int timerThird = 1000;
        for (char i : strThird.toCharArray()) {
            new Handler().postDelayed(() -> {
                String text = textView.getText() + Character.toString(i);
                Spannable textSpan = new SpannableString(text);
                if(text.length() >= startIndexThird && text.length() <= stopIndexThird) textSpan.setSpan(new ForegroundColorSpan(getColor(R.color.red)), startIndexThird, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                else if(text.length() > stopIndexThird) textSpan.setSpan(new ForegroundColorSpan(getColor(R.color.red)), startIndexThird, stopIndexThird, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(textSpan);
                }, timerThird+timerSecond+timerFirst);
            if(i == ' ') timerThird += 200;
            else timerThird += 100;
        }

        new Handler().postDelayed(() -> {
            Animation anim = AnimationUtils.loadAnimation(this,R.anim.appearance);
            buttonNext.startAnimation(anim);
            buttonNext.setVisibility(View.VISIBLE);
            buttonNext.setOnClickListener(view -> {
                SharedPreferences.Editor valueAdd = settings.edit();
                valueAdd.putBoolean("first_start", false);
                valueAdd.apply();
                Intent intent = new Intent(HelloActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
            });
        }, timerThird+timerSecond+timerFirst);

    }
}