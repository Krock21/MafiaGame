package me.hwproj.mafiagame.impltest;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.phases.GamePhaseClient;
import me.hwproj.mafiagame.phases.GameState;
import me.hwproj.mafiagame.phases.PhaseFragment;

public class TestPhaseClient implements GamePhaseClient {

    @Override
    public PhaseFragment createFragment(Client client) {
        return new TestPhaseFragment(client);
    }
}
