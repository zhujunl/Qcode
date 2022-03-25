package com.miaxis.phone.data.db;


import com.miaxis.phone.data.dao.MxPersonDao;
import com.miaxis.phone.data.entity.MxPerson;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {MxPerson.class}, version = 2)
@TypeConverters({})
public abstract class DB extends RoomDatabase {

    public abstract MxPersonDao getMxPersonDao();

}
