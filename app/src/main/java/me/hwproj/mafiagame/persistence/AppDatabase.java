package me.hwproj.mafiagame.persistence;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.RoomDatabase;
import me.hwproj.mafiagame.persistence.PersistentString;

import java.util.List;

@Database(entities = {PersistentString.class, PersistentInteger.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PersistentStringDao persistentStringDao();
    public abstract PersistentIntegerDao persistentIntegerDao();
}