package me.hwproj.mafiagame.persistence;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "persistent_strings")
public class PersistentString {
    @PrimaryKey
    @ColumnInfo(name = "tag")
    @NonNull
    private String tag;
    @ColumnInfo(name = "value")
    @NonNull
    private String value;

    public PersistentString(@NotNull String tag, @NotNull String value) {
        this.tag = tag;
        this.value = value;
    }

    @NotNull
    public String getValue() {
        return value;
    }

    public void setValue(@NotNull String value) {
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


