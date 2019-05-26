package me.hwproj.mafiagame.impltest;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.IOException;

import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.phases.GamePhaseClient;
import me.hwproj.mafiagame.phases.GameState;
import me.hwproj.mafiagame.phases.PhaseFragment;

public class TestPhaseClient implements GamePhaseClient {

    @Override
    public PhaseFragment createFragment(Client client) {
        return new TestPhaseFragment(client);
    }

    @Override
    public GameState deserializeGameState(DataInputStream dataStream) throws DeserializationException {
        try {
            return new TestPhaseGameState(dataStream.readInt());
        } catch (IOException e) {
            throw new DeserializationException(e);
        }
    }
}
