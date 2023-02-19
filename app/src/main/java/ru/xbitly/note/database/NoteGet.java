package ru.xbitly.note.database;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import ru.xbitly.note.R;
import ru.xbitly.note.activities.MainActivity;
import ru.xbitly.note.recycler.NotesListAdapter;


public class NoteGet extends AsyncTask<Void, Void, List<Note>>{

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    @SuppressLint("StaticFieldLeak")
    private final RecyclerView recyclerView;
    @SuppressLint("StaticFieldLeak")
    private final TextView textView;
    @SuppressLint("StaticFieldLeak")
    private final TextView textViewNotFound;
    @SuppressLint("StaticFieldLeak")
    private final EditText editText;
    @SuppressLint("StaticFieldLeak")
    private final NestedScrollView scroll_view;
    private NfcAdapter nfcAdapter;
    private List<Note> notes;
    private NotesListAdapter adapter;
    private final String sort;
    private final Paint p = new Paint();

    public NoteGet(Context context, RecyclerView recyclerView, TextView textView, TextView textViewNotFound, EditText editText, String sort, NestedScrollView scroll_view){
        this.context = context;
        this.recyclerView = recyclerView;
        this.textView = textView;
        this.textViewNotFound = textViewNotFound;
        this.editText = editText;
        this.sort = sort;
        this.scroll_view = scroll_view;
    }

    @Override
    protected List<Note> doInBackground(Void... voids) {
        if(sort.equals("TASC")){
            return DatabaseClient
                    .getInstance(context)
                    .getAppDatabase()
                    .noteDao()
                    .getAllSortTitleASC();
        } else if (sort.equals("TDESC")){
            return DatabaseClient
                    .getInstance(context)
                    .getAppDatabase()
                    .noteDao()
                    .getAllSortTitleDESC();
        } else {
            return DatabaseClient
                    .getInstance(context)
                    .getAppDatabase()
                    .noteDao()
                    .getAll();
        }
    }

    @Override
    protected void onPostExecute(List<Note> notes) {
        super.onPostExecute(notes);
        this.notes = notes;
        load();
        if (sort.equals("DASC")){
            notes.sort((note1, note2) -> Float.compare(note1.getDateLong(), note2.getDateLong()));
        } else if (sort.equals("DDESC")){
            notes.sort((note1, note2) -> Float.compare(note1.getDateLong(), note2.getDateLong()));
            Collections.reverse(notes);
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter.search(editable.toString());
                if (adapter.getItemCount() == 0) {
                    textViewNotFound.setVisibility(RelativeLayout.VISIBLE);
                } else {
                    textViewNotFound.setVisibility(RelativeLayout.GONE);
                }
            }
        });
        enableSwipe();
    }

    private void enableSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT){
                    final Note deletedNote = notes.get(position);
                    final int deletedPosition = position;
                    adapter.removeItem(position);
                    if (adapter.getItemCount() == 0) {
                        textViewNotFound.setVisibility(RelativeLayout.VISIBLE);
                    } else {
                        textViewNotFound.setVisibility(RelativeLayout.GONE);
                    }

                    Snackbar snackbar = Snackbar.make(scroll_view, "", Snackbar.LENGTH_LONG);

                    Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

                    @SuppressLint("InflateParams")
                    View snackView = LayoutInflater.from(context).inflate(R.layout.view_snackbar, null);

                    RelativeLayout buttonUndo = snackView.findViewById(R.id.button_undo);
                    buttonUndo.setOnClickListener(v -> {
                        adapter.restoreItem(deletedNote, deletedPosition);
                        snackbar.dismiss();
                        if (adapter.getItemCount() == 0) {
                            textViewNotFound.setVisibility(RelativeLayout.VISIBLE);
                        } else {
                            textViewNotFound.setVisibility(RelativeLayout.GONE);
                        }
                    });
                    layout.addView(snackView, 0);

                    int px = dpToPx(22);

                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackbar.getView().getLayoutParams();
                    params.setMargins(px, px, px, px);
                    snackbar.getView().setLayoutParams(params);

                    snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
                    snackbar.show();

                    snackbar.show();
                    snackbar.addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            if (!adapter.itemIsRestored()){
                                NoteDelete noteDelete = new NoteDelete(context, deletedNote);
                                noteDelete.execute();
                            }
                        }

                        @Override
                        public void onShown(Snackbar snackbar) {
                        }
                    });
                } else {
                    final Note shareNote = notes.get(position);
                    final String textShare = shareNote.getTitle() + "\n\n" + shareNote.getText();

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, textShare);
                    intent.setType("text/plane");
                    context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.share)));

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX < 0) {
                        p.setColor(context.getColor(R.color.red));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_delete_main);
                        icon = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(icon);
                        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        drawable.draw(canvas);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(context.getColor(R.color.blue));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_share);
                        icon = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(icon);
                        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        drawable.draw(canvas);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public int dpToPx(int dp) {
        context.getResources();
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private void load(){
        adapter = new NotesListAdapter(this.notes, this.context);
        recyclerView.setAdapter(adapter);
        int count = adapter.getItemCount();
        String str;
        if(count == 1) str = 1 + " " + context.getResources().getString(R.string.note_);
        else str = count + " " + context.getResources().getString(R.string.notes_);
        textView.setText(str);

        if (notes.isEmpty()) {
            textViewNotFound.setVisibility(RelativeLayout.VISIBLE);
        } else {
            textViewNotFound.setVisibility(RelativeLayout.GONE);
        }
    }
}
