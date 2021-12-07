package com.example.dictionaryapp.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.dictionaryapp.data.sources.local.room.AppDatabase;
import com.example.dictionaryapp.data.sources.local.room.enteties.Word;

import java.util.concurrent.Executors;

public class App extends Application {
    public static App instance;
    public static AppDatabase appDatabase;

    public static SharedPreferences storage;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appDatabase = AppDatabase.getDataBase(this);
        storage = getSharedPreferences("LocalStorage", Context.MODE_PRIVATE);

        if (storage.getBoolean("isFirst", true)) {
            storage.edit().putBoolean("isFirst", false).apply();
            Executors.newSingleThreadExecutor().execute(() -> {
                        appDatabase.getWordDao().insertAll(
                                new Word(
                                        "autobiography",
                                        "avtobiografiya",
                                        "",
                                        false
                                ),
                                new Word(
                                        "pamphlet",
                                        "risola",
                                        "",
                                        false
                                ),
                                new Word(
                                        "environment ",
                                        "muhit",
                                        "",
                                        false
                                ),
                                new Word(
                                        "An essay is nothing but a piece of content which is written from the perception of writer or author. ",
                                        "Insho yozuvchi yoki muallifning idrokidan kelib chiqqan holda yozilgan mazmundan boshqa narsa emas.",
                                        "",
                                        false
                                ),
                                new Word(
                                        "Following is a great list of 100 essay topics.",
                                        "Quyida 100 ta insho mavzularining ajoyib roʻyxati keltirilgan.",
                                        "",
                                        false
                                ),
                                new Word(
                                        "We will be adding 400 more soon!",
                                        "Tez orada yana 400 ta qo'shamiz!",
                                        "",
                                        false
                                ));

                        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("refresh"));
                    }
            );
        }
    }
}
