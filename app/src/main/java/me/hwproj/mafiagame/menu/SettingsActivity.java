package me.hwproj.mafiagame.menu;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import me.hwproj.mafiagame.R;
import me.hwproj.mafiagame.persistence.AppDatabaseInteractor;
import me.hwproj.mafiagame.persistence.PersistentString;
import me.hwproj.mafiagame.persistence.PersistentStringDao;

/**
 * An activity where user can change app's settings.
 * Currently the only setting is player's in-game name.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        AppDatabaseInteractor di = new AppDatabaseInteractor(getApplicationContext());

        EditText name = findViewById(R.id.myName);
        name.setText(di.loadName());
        Button setName = findViewById(R.id.setName);
        setName.setOnClickListener(v -> {
            di.saveName(name.getText().toString());
        });
    }
}
