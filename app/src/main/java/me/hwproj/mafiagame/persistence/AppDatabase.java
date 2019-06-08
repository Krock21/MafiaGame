package me.hwproj.mafiagame.persistence;
import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {PersistentString.class, PersistentInteger.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PersistentStringDao persistentStringDao();
    public abstract PersistentIntegerDao persistentIntegerDao();
}