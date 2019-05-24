package me.hwproj.mafiagame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

/**
 * A room to configure a game instance, BEFORE connecting to clients.
 * Basically select minimum and maximum amounts of people in the game
 */
public class GameCreate extends AppCompatActivity {

    int minPlayerCount = 1;
    int maxPlayerCount = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_create);

        Button config = findViewById(R.id.configure);
        config.setOnClickListener(v -> {
            minPlayerCount = 1;
            maxPlayerCount = 2;
        });


        Button start = findViewById(R.id.startConnect); // a button for you
        start.setOnClickListener(v -> {
            startActivity(new Intent(this, PhaseActivity.class));
        });
    }
}
