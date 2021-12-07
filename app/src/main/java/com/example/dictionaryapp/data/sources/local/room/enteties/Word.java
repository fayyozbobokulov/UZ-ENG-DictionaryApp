package com.example.dictionaryapp.data.sources.local.room.enteties;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "word")
public class Word implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "word")
    public String word;

    @ColumnInfo(name = "translate")
    public String translate;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "is_saved")
    public Boolean isSaved;

    @Override
    public String toString() {
        return "Word{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", translate='" + translate + '\'' +
                ", date=" + date +
                ", isSaved=" + isSaved +
                '}';
    }

    public Word(String word, String translate, String date, Boolean isSaved) {
        this.word = word;
        this.translate = translate;
        this.date = date;
        this.isSaved = isSaved;
    }
}
