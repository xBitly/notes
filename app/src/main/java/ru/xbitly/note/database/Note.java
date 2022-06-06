package ru.xbitly.note.database;

import android.annotation.SuppressLint;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Entity
public class Note implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "text")
    private String text;

    @ColumnInfo(name = "date")
    private String date;

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public long getDateLong(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Objects.requireNonNull(convertedDate).getTime();
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
