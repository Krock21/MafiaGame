package me.hwproj.mafiagame.phases;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.phases.GameState;

public abstract class PhaseActivity extends AppCompatActivity {

    protected abstract void processGameState(GameState state);

    /**
     * <code>setContentView</code> here
     * Don't bind <code>processGameState</code>, it will be bound in <code>onCreate</code>
     */
    protected abstract void initialize();

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();

        Intent intent = getIntent();
        thisPhaseNumber = intent.getIntExtra(Client.THIS_PHASE_NUMBER, -1);

        Client client = Client.getClient(this);

        client.getLatestGameState().observe(this, this::processGameState);
        processGameState(client.getLatestGameState().getValue());

        client.getPhaseNumberData().observe(this, this::dealWithPhaseNumber);
        dealWithPhaseNumber(client.getPhaseNumberData().getValue());
    }

    private void dealWithPhaseNumber(Integer number) {
        if (number < thisPhaseNumber || thisPhaseNumber == -1) {
            Log.d("Bad", "Wrong phase number:" + thisPhaseNumber + " -> " + number);
        }

        if (number > thisPhaseNumber) {
            Log.d("Ok", "transition to next phase:" + thisPhaseNumber + " -> " + number);
            startActivity(Client.getClient(this).nextPhaseActivity(this));
        }
    }

    private int thisPhaseNumber;
}
