package com.example.dictionaryapp.data.sources.local.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.dictionaryapp.data.sources.local.room.daos.WordDao;
import com.example.dictionaryapp.data.sources.local.room.enteties.Word;

@Database(
        entities = {Word.class},
        version = 1
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract WordDao getWordDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDataBase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder
                            (context.getApplicationContext(), AppDatabase.class, "dictionary.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
