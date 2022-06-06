package ru.xbitly.note.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.xbitly.note.R;
import ru.xbitly.note.database.Note;
import ru.xbitly.note.database.NoteGet;

public class MainActivity extends AppCompatActivity {

    private long backPressedTime;

    @SuppressLint({"NonConstantResourceId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SharedPreferences settings = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean night_mode = settings.getBoolean("night_mode", false);
        String sort = settings.getString("sort", "DDESC");
        if(night_mode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        RelativeLayout buttonNew = findViewById(R.id.button_new);
        ImageButton buttonSort = findViewById(R.id.button_sort);
        ImageButton buttonChangeTheme = findViewById(R.id.button_change_theme);
        EditText editTextSearch = findViewById(R.id.edit_text_search);
        TextView textViewCount = findViewById(R.id.text_count);
        TextView textViewNotFound = findViewById(R.id.text_not_found);
        NestedScrollView scrollView = findViewById(R.id.scroll_view);

        if (night_mode){
            buttonChangeTheme.setImageResource(R.drawable.ic_white_theme);
        } else {
            buttonChangeTheme.setImageResource(R.drawable.ic_black_theme);
        }

        switch (sort) {
            case "DDESC":
                buttonSort.setImageResource(R.drawable.ic_sort_ddesc);
                break;
            case "DASC":
                buttonSort.setImageResource(R.drawable.ic_sort_dasc);
                break;
            case "TDESC":
                buttonSort.setImageResource(R.drawable.ic_sort_tdesc);
                break;
            default:
                buttonSort.setImageResource(R.drawable.ic_sort_tasc);
                break;
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);

        NoteGet noteGet = new NoteGet(MainActivity.this, recyclerView, textViewCount, textViewNotFound, editTextSearch, sort, scrollView);
        noteGet.execute();

        SharedPreferences.Editor settingsEditor = settings.edit();

        buttonSort.setOnClickListener(view -> {
            switch (settings.getString("sort", "DDESC")) {
                case "DDESC":
                    NoteGet noteGetDD = new NoteGet(MainActivity.this, recyclerView, textViewCount, textViewNotFound, editTextSearch, "DASC", scrollView);
                    noteGetDD.execute();
                    settingsEditor.putString("sort", "DASC");
                    settingsEditor.apply();
                    buttonSort.setImageResource(R.drawable.ic_sort_dasc);
                    break;
                case "DASC":
                    NoteGet noteGetDA = new NoteGet(MainActivity.this, recyclerView, textViewCount, textViewNotFound, editTextSearch, "TDESC", scrollView);
                    noteGetDA.execute();
                    settingsEditor.putString("sort", "TDESC");
                    settingsEditor.apply();
                    buttonSort.setImageResource(R.drawable.ic_sort_tdesc);
                    break;
                case "TDESC":
                    NoteGet noteGetTD = new NoteGet(MainActivity.this, recyclerView, textViewCount, textViewNotFound, editTextSearch, "TASC", scrollView);
                    noteGetTD.execute();
                    settingsEditor.putString("sort", "TASC");
                    settingsEditor.apply();
                    buttonSort.setImageResource(R.drawable.ic_sort_tasc);
                    break;
                default:
                    NoteGet noteGetTA = new NoteGet(MainActivity.this, recyclerView, textViewCount, textViewNotFound, editTextSearch, "DDESC", scrollView);
                    noteGetTA.execute();
                    settingsEditor.putString("sort", "DDESC");
                    settingsEditor.apply();
                    buttonSort.setImageResource(R.drawable.ic_sort_ddesc);
                    break;
            }
        });

        editTextSearch.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            v.requestFocus();
            return false;
        });

        editTextSearch.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (!keyboardShown(editTextSearch.getRootView())) {
                editTextSearch.setFocusable(false);
                editTextSearch.setFocusableInTouchMode(false);
            }
        });

        buttonNew.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
            finish();
        });

        buttonChangeTheme.setOnClickListener(view -> {
            settingsEditor.putBoolean("night_mode", !night_mode);
            settingsEditor.apply();
            recreate();
        });
    }

    @Override
    public void recreate() {
        finish();
        overridePendingTransition(R.anim.appearance,
                R.anim.disappearance);
        startActivity(getIntent());
        overridePendingTransition(R.anim.appearance,
                R.anim.disappearance);
    }

    private boolean keyboardShown(View rootView) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()){
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        } else {
            Toast.makeText(this, "click again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

}