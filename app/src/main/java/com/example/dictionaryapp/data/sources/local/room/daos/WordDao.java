package com.example.dictionaryapp.data.sources.local.room.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.dictionaryapp.data.sources.local.room.enteties.Word;

import java.util.List;

@Dao
public interface WordDao {

    @Query("SELECT * FROM word WHERE is_saved = 1 ORDER BY word ASC")
    List<Word> getAllSaved();

    @Query("SELECT * FROM word WHERE word LIKE '%' || :query || '%' ORDER BY word ASC")
    List<Word> searchByWord(String query);

    @Query("SELECT * FROM word WHERE translate LIKE '%' || :query || '%' ORDER BY word ASC")
    List<Word> searchByTranslation(String query);

    @Insert
    void insertAll(Word... words);

    @Insert
    void insert(Word word);

    @Update
    void update(Word word);

    @Delete
    void delete(Word user);
}
