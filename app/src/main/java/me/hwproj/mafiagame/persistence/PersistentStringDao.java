package me.hwproj.mafiagame.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PersistentStringDao {
    @Query("SELECT * FROM persistent_strings WHERE tag = :tag LIMIT 1")
    PersistentString getByTag(String tag);
    @Insert
    void insertAll(PersistentString... strings);
    @Delete
    void delete(PersistentString string);
}


