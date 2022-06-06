package ru.xbitly.note.database;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import ru.xbitly.note.activities.MainActivity;

public class NoteDelete extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private final Note note;

    public NoteDelete(Context context, Note note){
        this.context = context;
        this.note = note;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        DatabaseClient.getInstance(context).getAppDatabase()
                .noteDao()
                .delete(note);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
