package me.hwproj.mafiagame.persistence;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "persistent_integers")
public class PersistentInteger {
    @PrimaryKey
    @ColumnInfo(name = "tag")
    @NonNull
    private String tag;
    @ColumnInfo(name = "value")
    @NonNull
    private Integer value;


    public PersistentInteger(@NotNull String tag, @NotNull Integer value) {
        this.tag = tag;
        this.value = value;
    }

    @NotNull
    public Integer getValue() {
        return value;
    }

    public void setValue(@NotNull Integer value) {
        this.value = value;
    }

    @NotNull
    public String getTag() {
        return tag;
    }

    public void setTag(@NotNull String tag) {
        this.tag = tag;
    }
}
