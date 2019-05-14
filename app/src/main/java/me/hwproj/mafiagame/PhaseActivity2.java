package me.hwproj.mafiagame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.phases.GameState;
import me.hwproj.mafiagame.phases.PhaseFragment;

public class PhaseActivity2 extends AppCompatActivity {

    private PhaseFragment currentPhaseFragment;
    private GameState lastGameState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phase2);

        Intent intent = getIntent();
        thisPhaseNumber = intent.getIntExtra(Client.THIS_PHASE_NUMBER, -1);


        Client client = Client.getClient(this);

        client.getLatestGameState().observe(this, this::processGameState);
        processGameState(client.getLatestGameState().getValue());

        client.getPhaseNumberData().observe(this, this::dealWithPhaseNumber);
        dealWithPhaseNumber(client.getPhaseNumberData().getValue());
    }

    private void processGameState(GameState gameState) {
        lastGameState = gameState;
        if (currentPhaseFragment != null) {
            currentPhaseFragment.processGameState(gameState);
        }
    }

    private void dealWithPhaseNumber(Integer number) {
        if (number < thisPhaseNumber || thisPhaseNumber == -1) {
            Log.d("Bad", "Wrong phase number:" + thisPhaseNumber + " -> " + number);
        }

        if (number > thisPhaseNumber) {
            Log.d("Ok", "transition to next phase:" + thisPhaseNumber + " -> " + number);
            startPhaseFragment(Client.getClient(this).nextPhaseFragment(this));
        }
    }

    private void startPhaseFragment(PhaseFragment fg) {
        currentPhaseFragment = fg;
        currentPhaseFragment.processGameState(lastGameState);
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentLayout, fg).commit();
        findViewById(R.id.testid);
    }

    private int thisPhaseNumber;
}

