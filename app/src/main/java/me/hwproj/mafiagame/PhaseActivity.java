package me.hwproj.mafiagame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;

import me.hwproj.mafiagame.content.phases.vote.VotePhase;
import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.gameflow.PlayerSettings;
import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.gameflow.ServerGameData;
import me.hwproj.mafiagame.gameflow.Settings;
import me.hwproj.mafiagame.gameplay.Role;
import me.hwproj.mafiagame.impltest.NetworkSimulator;
import me.hwproj.mafiagame.impltest.TestPhase;
import me.hwproj.mafiagame.phases.GameState;
import me.hwproj.mafiagame.phases.PhaseFragment;

public class PhaseActivity extends AppCompatActivity {

    private PhaseFragment currentPhaseFragment;
    private GameState lastGameState;
    private Client client;

    private void startClient() {
        NetworkSimulator net = new NetworkSimulator();

        Settings settings = new Settings();
        settings.phases = Arrays.asList(new TestPhase(), new VotePhase());
        settings.playerSettings = Arrays.asList(
//                new PlayerSettings(Role.CITIZEN, "Pathfinder"),
//                new PlayerSettings(Role.HEALER, "Lifeline"),
                new PlayerSettings(Role.MAFIA, "Caustic")
        );


        client = new Client(net, settings, 0);
        Server server = new Server(settings, net);

        net.start(client, server);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phase);

        startClient(); // TODO

        thisPhaseNumber = -1;

        client.startNextPhase(0);

        client.getLatestGameState().observe(this, this::processGameState);
        processGameState(client.getLatestGameState().getValue());

        client.getPhaseNumberData().observe(this, this::dealWithPhaseNumber);
        dealWithPhaseNumber(client.getPhaseNumberData().getValue());

        Button b = findViewById(R.id.testid); // TODO delete
        b.setOnClickListener(v -> client.startNextPhase(thisPhaseNumber + 1));
    }

    private void processGameState(GameState gameState) {
        lastGameState = gameState;
        if (currentPhaseFragment != null) {
            currentPhaseFragment.processGameState(gameState);
        }
    }

    private void dealWithPhaseNumber(Integer number) {
        if (number < thisPhaseNumber) {
            Log.d("Bad", "Wrong phase number:" + thisPhaseNumber + " -> " + number);
        }

        if (number > thisPhaseNumber) {
            Log.d("Ok", "transition to next phase:" + thisPhaseNumber + " -> " + number);
            startPhaseFragment(client.nextPhaseFragment(this));
            thisPhaseNumber = number;
        }
    }

    private void startPhaseFragment(PhaseFragment fg) {
        if (currentPhaseFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(currentPhaseFragment).commit();
        }
        currentPhaseFragment = fg;
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentLayout, fg).commit();

        Button b = findViewById(R.id.testid); // TODO delete
        b.setText(fg.getClass().getName());
    }

    private int thisPhaseNumber;
}

