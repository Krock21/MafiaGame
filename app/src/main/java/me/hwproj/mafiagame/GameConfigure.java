package me.hwproj.mafiagame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * A room to configure a game instance, after all the clients are connected.
 */
public class GameConfigure extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_configure);

        //Button start = findViewById(R.id.startTest);
        //start.setOnClickListener(this::startTestPhase);
    }

    private void startTestPhase(View v) {
        startActivity(new Intent(this, PhaseActivity.class));
    }
}
