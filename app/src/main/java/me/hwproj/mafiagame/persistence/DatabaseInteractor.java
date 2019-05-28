package me.hwproj.mafiagame.persistence;

import android.content.Context;

import androidx.room.Room;

public class DatabaseInteractor {
    private AppDatabase database;

    public DatabaseInteractor(Context context) {
        database = Room
                .databaseBuilder(context, AppDatabase.class, "MafiaGameDatabase")
                .build();
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
