package ru.xbitly.note.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    @Query("SELECT * FROM Note")
    List<Note> getAll();

    @Query("SELECT * FROM Note ORDER BY title ASC")
    List<Note> getAllSortTitleASC();

    @Query("SELECT * FROM Note ORDER BY title DESC")
    List<Note> getAllSortTitleDESC();

    @Insert
    void insert(Note note);

    @Delete
    void delete(Note note);

    @Update
    void update(Note note);

}