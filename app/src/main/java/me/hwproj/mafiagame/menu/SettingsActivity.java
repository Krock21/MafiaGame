package me.hwproj.mafiagame.menu;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import me.hwproj.mafiagame.R;
import me.hwproj.mafiagame.persistence.AppDatabaseInteractor;

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
        setName.setOnClickListener(v -> di.saveName(name.getText().toString()));
    }
}
