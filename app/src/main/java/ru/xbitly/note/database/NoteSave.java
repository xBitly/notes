package ru.xbitly.note.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

public class NoteSave extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private final Note note;

    public NoteSave(Context context, Note note){
        this.context = context;
        this.note = note;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        DatabaseClient.getInstance(context.getApplicationContext()).getAppDatabase()
                .noteDao()
                .insert(note);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}