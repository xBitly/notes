package ru.xbitly.note.recycler;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.xbitly.note.R;
import ru.xbitly.note.activities.MainActivity;
import ru.xbitly.note.activities.NoteActivity;
import ru.xbitly.note.database.Note;
import ru.xbitly.note.database.NoteDelete;


public class NotesListAdapter extends RecyclerView.Adapter<NotesRecyclerViewHolder> {

    private List<Note> notes;
    private final List<Note> notes_;
    private final Context context;
    private boolean restored = false;

    public NotesListAdapter(List<Note> notes, Context context) {
        this.notes = notes;
        this.notes_ = notes;
        this.context = context;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.view_note;
    }

    @NonNull
    @Override
    public NotesRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new NotesRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotesRecyclerViewHolder holder, final int position) {
        holder.getTextViewTitle().setText(notes.get(position).getTitle());
        holder.getTextView().setText(notes.get(position).getText());
        holder.getTextViewDate().setText(notes.get(position).getDate());
        if (position == getItemCount() - 1) holder.getViewLine().setVisibility(View.INVISIBLE);
        holder.getViewItem().setOnClickListener(view -> {
            Intent intent = new Intent(context, NoteActivity.class);
            intent.putExtra("note", notes.get(position));
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
            ((Activity) context).finish();
        });
    }

    public void removeItem(int position){
        this.notes.remove(position);
        notifyDataSetChanged();
        restored = false;
    }

    public void restoreItem(Note note, int position) {
        this.notes.add(position, note);
        notifyDataSetChanged();
        restored = true;
    }

    public boolean itemIsRestored(){
        return restored;
    }

    public void search(String text) {
        List<Note> newNotes = new ArrayList<>();
        text = text.toLowerCase();
        for (Note note : notes_) {
            if (note.getText().toLowerCase().contains(text) || note.getTitle().toLowerCase().contains(text)) {
                newNotes.add(note);
            }
        }
        this.notes = newNotes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

}