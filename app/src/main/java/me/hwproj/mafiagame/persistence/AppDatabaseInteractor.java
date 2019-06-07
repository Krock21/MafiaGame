package me.hwproj.mafiagame.persistence;

import android.content.Context;

import java.util.Random;

public class AppDatabaseInteractor {
    private static final String NAME_TAG = "name";
    private final AppDatabase database;

    public AppDatabaseInteractor(Context context) {
        database = new DatabaseInteractor(context).getDatabase();
    }

    public String loadName() {
        PersistentString d = database.persistentStringDao().getByTag(NAME_TAG);
        if (d == null) {
            String newName = "Bob" + new Random().nextInt(1000); // TODO maybe move name genereation
            saveName(newName);
            return newName;
        }
        return d.getValue();
    }

    public void saveName(String name) {
        PersistentStringDao dao = database.persistentStringDao();
        if(dao.getByTag("name") != null) {
            dao.delete(dao.getByTag("name"));
        }
        dao.insertAll(new PersistentString("name", name));
    }
}
