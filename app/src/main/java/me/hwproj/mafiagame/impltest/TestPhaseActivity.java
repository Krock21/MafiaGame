package me.hwproj.mafiagame.impltest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;
import me.hwproj.mafiagame.R;
import me.hwproj.mafiagame.TestStringHolder;
import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.phases.GameState;

public class TestPhaseActivity extends me.hwproj.mafiagame.phases.PhaseActivity {

    private int thisPhaseNumber;

    protected void processGameState(GameState state) {
        if (!(state instanceof TestPhaseGameState)) {
            return;
        }
        ViewModelProviders.of(this).get(TestStringHolder.class)
                .setText(String.valueOf(((TestPhaseGameState) state).getSum()));
    }

    @Override
    protected void initialize() {
        setContentView(R.layout.activity_test_phase);
        Client client = Client.getClient(this);

        TextView text = findViewById(R.id.sumView);
        TestStringHolder h = ViewModelProviders.of(this).get(TestStringHolder.class);
        h.getData().observe(this, text::setText);

        Button b = findViewById(R.id.increaseSum);
        b.setOnClickListener(v -> client.sendPlayerAction(new TestPhasePlayerAction(2)));

        Button bnext = findViewById(R.id.nextButton);
        bnext.setOnClickListener(v -> client.sendPlayerAction(TestPhasePlayerAction.nextPhase()));
    }
}
