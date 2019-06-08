package me.hwproj.mafiagame.persistence;

import android.content.Context;

import java.util.Random;

/**
 * All of the application's interactions with database should be through this class.
 *
 * It's interactions with database happen in the calling thread, but are fast enough
 * so that UI won't lag if called from UI thread
 */
public class AppDatabaseInteractor {
    private static final String NAME_TAG = "name";
    private final AppDatabase database;

    public AppDatabaseInteractor(Context context) {
        database = new DatabaseInteractor(context).getDatabase();
    }

    /**
     * Loads player's nickname from the database or creates it if needed
     * @return player's name
     */
    public String loadName() {
        PersistentString d = database.persistentStringDao().getByTag(NAME_TAG);
        if (d == null) {
            String newName = "Bob" + new Random().nextInt(1000);
            saveName(newName);
            return newName;
        }
        return d.getValue();
    }

    /**
     * Saves player's name to the database
     * @param name player's name to set
     */
    public void saveName(String name) {
        PersistentStringDao dao = database.persistentStringDao();
        if(dao.getByTag("name") != null) {
            dao.delete(dao.getByTag("name"));
        }
        dao.insertAll(new PersistentString("name", name));
    }
}
