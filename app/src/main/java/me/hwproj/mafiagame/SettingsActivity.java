package me.hwproj.mafiagame;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import me.hwproj.mafiagame.persistence.PersistentString;
import me.hwproj.mafiagame.persistence.PersistentStringDao;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText name = findViewById(R.id.myName);
        PersistentStringDao stringDao = MainActivity.getDatabaseInteractor().getDatabase().persistentStringDao();
        if(stringDao.getByTag("name") != null) {
            name.setText(stringDao.getByTag("name").getValue());
        }
        Button setName = findViewById(R.id.setName);
        setName.setOnClickListener(v -> {
            PersistentStringDao dao = MainActivity.getDatabaseInteractor().getDatabase().persistentStringDao();
            if(dao.getByTag("name") != null) {
                dao.delete(dao.getByTag("name"));
            }
            dao.insertAll(new PersistentString("name", name.getText().toString()));
        });
    }
}
