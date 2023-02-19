package ru.xbitly.note.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.xbitly.note.R;
import ru.xbitly.note.database.Note;
import ru.xbitly.note.database.NoteDelete;
import ru.xbitly.note.database.NoteEdit;
import ru.xbitly.note.database.NoteSave;

public class NoteActivity extends AppCompatActivity {

    private EditText editTextTitle, editText;
    private RelativeLayout buttonSave, buttonDelete;
    private TextView textViewCount, textViewButtonSave;
    private ImageView imageViewButtonSave;
    private Note note;

    private String titleOld, textOld;
    private boolean flag = true;
    private boolean flag_edit = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SharedPreferences settings = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean night_mode = settings.getBoolean("night_mode", false);
        if(night_mode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        note = (Note) getIntent().getSerializableExtra("note");
        if (note == null) {
            flag = false;
            note = new Note();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        editTextTitle = findViewById(R.id.edit_text_title);
        editText = findViewById(R.id.edit_text);
        buttonSave = findViewById(R.id.button_save);
        buttonDelete = findViewById(R.id.button_delete);
        textViewCount = findViewById(R.id.text_count);
        textViewButtonSave = findViewById(R.id.text_save);
        imageViewButtonSave = findViewById(R.id.image_save);
        ImageButton buttonBack = findViewById(R.id.button_back);

        if (note != null){
            editText.setText(note.getText());
            editTextTitle.setText(note.getTitle());
        }

        editTextTitle.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            v.requestFocus();
            return false;
        });

        editText.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            v.requestFocus();
            return false;
        });

        editTextTitle.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (!keyboardShown(editTextTitle.getRootView())) {
                editTextTitle.setFocusable(false);
                editTextTitle.setFocusableInTouchMode(false);
            }
        });

        editText.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (!keyboardShown(editText.getRootView())) {
                editText.setFocusable(false);
                editText.setFocusableInTouchMode(false);
            }
        });

        titleOld = editTextTitle.getText().toString();
        textOld = editText.getText().toString();

        checkButtons();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkButtons();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        KeyListener editTextKeyListener = editText.getKeyListener();
        KeyListener editTextTitleKeyListener = editTextTitle.getKeyListener();

        if (editText.getText().toString().equals(textOld) && editTextTitle.getText().toString().equals(titleOld) && !editText.getText().toString().isEmpty()) {
            textViewButtonSave.setText(getResources().getString(R.string.edit));
            imageViewButtonSave.setImageResource(R.drawable.ic_edit);
            editText.setKeyListener(null);
            editTextTitle.setKeyListener(null);
            flag_edit = true;
        }

        buttonSave.setOnClickListener(view -> {
            if(!flag_edit) {
                onBackPressed();
            } else {
                textViewButtonSave.setText(getResources().getString(R.string.save));
                imageViewButtonSave.setImageResource(R.drawable.ic_save);
                editText.setKeyListener(editTextKeyListener);
                editTextTitle.setKeyListener(editTextTitleKeyListener);
                flag_edit = false;
            }
        });

        buttonDelete.setOnClickListener(view -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(NoteActivity.this);
            LayoutInflater inflater = LayoutInflater.from(dialog.getContext());
            View warning = inflater.inflate(R.layout.view_alert_dialog, null);
            dialog.setView(warning);
            RelativeLayout buttonCancel, buttonConfirm;
            buttonCancel = warning.findViewById(R.id.cancel_button);
            buttonConfirm = warning.findViewById(R.id.confirm_button);

            AlertDialog alert = dialog.create();
            buttonCancel.setOnClickListener(view_ -> alert.dismiss());
            buttonConfirm.setOnClickListener(view_ -> {
                if(flag) {
                    NoteDelete noteDelete = new NoteDelete(this, note);
                    noteDelete.execute();
                }
                Intent intent = new Intent(NoteActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.disappearance);
                finish();
            });
            InsetDrawable inset = new InsetDrawable(getDrawable(R.drawable.background_alert_dialog), dpToPx(22));
            alert.getWindow().setBackgroundDrawable(inset);
            alert.show();
        });

        buttonBack.setOnClickListener(view -> onBackPressed());

        editTextTitle.addTextChangedListener(textWatcher);
        editText.addTextChangedListener(textWatcher);

    }

    private void checkButtons(){
        if (editText.getText().toString().isEmpty()){
            buttonSave.setVisibility(View.INVISIBLE);
            buttonDelete.setVisibility(View.INVISIBLE);
        } else if (editText.getText().toString().equals(textOld) && editTextTitle.getText().toString().equals(titleOld)){
            buttonSave.setVisibility(View.VISIBLE);
            buttonDelete.setVisibility(View.VISIBLE);
        } else {
            textViewButtonSave.setText(getResources().getString(R.string.save));
            imageViewButtonSave.setImageResource(R.drawable.ic_save);
            flag_edit = false;
            buttonSave.setVisibility(View.VISIBLE);
            buttonDelete.setVisibility(View.VISIBLE);
        }
        int count = editText.getText().toString().replace("\n", " ").split(" ").length;
        if (editText.getText().toString().isEmpty()) count = 0;
        String str;
        if(count == 1) str = 1 + " " + getResources().getString(R.string.word_);
        else str = count + " " + getResources().getString(R.string.words_);
        textViewCount.setText(str);
    }

    public int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private boolean keyboardShown(View rootView) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

    private void save(){
        note.setText(editText.getText().toString());
        if (editTextTitle.getText().toString().isEmpty()) note.setTitle(getResources().getString(R.string.untitled));
        else note.setTitle(editTextTitle.getText().toString());
        String currentDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
        note.setDate(currentDate);
        if (!editText.getText().toString().isEmpty() && !flag){
            NoteSave noteSave = new NoteSave(this, note);
            noteSave.execute();
        } else if(!editText.getText().toString().isEmpty()){
            NoteEdit noteEdit = new NoteEdit(this, note);
            noteEdit.execute();
        }
    }

    @Override
    public void onBackPressed() {
        save();
        Intent intent = new Intent(NoteActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0,R.anim.slide_out_right);
        finish();
    }
}