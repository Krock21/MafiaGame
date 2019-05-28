package me.hwproj.mafiagame.persistence;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "persistent_integers")
public class PersistentInteger {
    @PrimaryKey
    @ColumnInfo(name = "tag")
    @NonNull
    private String tag;
    @ColumnInfo(name = "value")
    private Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
