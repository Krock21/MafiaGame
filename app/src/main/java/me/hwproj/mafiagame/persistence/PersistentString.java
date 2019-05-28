package me.hwproj.mafiagame.persistence;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "persistent_strings")
public class PersistentString {
    @PrimaryKey
    @ColumnInfo(name = "tag")
    @NonNull
    private String tag;
    @ColumnInfo(name = "value")
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}


