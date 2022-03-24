package com.miaxis.bp990.data.dao;

import android.app.Application;

import com.miaxis.bp990.data.entity.Config;
import com.miaxis.bp990.data.entity.Person;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Person.class, Config.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DBName = "BpEntry.db";

    private static AppDatabase instance;

    public static AppDatabase getInstance() {
        return instance;
    }


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

    public abstract ConfigDao configdao();

}
