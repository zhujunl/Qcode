package com.miaxis.phone.data.dao;

import android.app.Application;
import android.app.Person;


import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Person.class}, version = 1)
@TypeConverters({})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DBName = "BpEntry.db";

    private static AppDatabase instance;

    public static AppDatabase getInstance() {
        return instance;
    }

    //should be init first
    public static void initDB(Application application) {
        instance = createDB(application);
    }

    private static AppDatabase createDB(Application application) {
        return Room.databaseBuilder(application, AppDatabase.class, DBName)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                    }

                    @Override
                    public void onOpen(@NonNull SupportSQLiteDatabase db) {
                        super.onOpen(db);
                    }
                })
                .fallbackToDestructiveMigration()
                .build();
    }

    public abstract PersonDao persondao();

}
