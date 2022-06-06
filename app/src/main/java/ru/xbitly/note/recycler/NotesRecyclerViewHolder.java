package ru.xbitly.note.recycler;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.xbitly.note.R;

public class NotesRecyclerViewHolder extends RecyclerView.ViewHolder {

    private final TextView textViewTitle, textView, textViewDate;
    private final View viewLine;
    private final RelativeLayout viewItem;

    public NotesRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewTitle = itemView.findViewById(R.id.text_title);
        textView = itemView.findViewById(R.id.text);
        textViewDate = itemView.findViewById(R.id.text_date);
        viewLine = itemView.findViewById(R.id.view_line);
        viewItem = itemView.findViewById(R.id.view_item);
    }

    public TextView getTextViewTitle() {
        return textViewTitle;
    }

    public TextView getTextView() {
        return textView;
    }

    public TextView getTextViewDate() {
        return textViewDate;
    }

    public View getViewLine() {
        return viewLine;
    }

    public RelativeLayout getViewItem() {
        return viewItem;
    }
}