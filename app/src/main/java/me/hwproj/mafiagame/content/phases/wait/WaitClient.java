package me.hwproj.mafiagame.content.phases.wait;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phases.GamePhaseClient;
import me.hwproj.mafiagame.phases.GameState;
import me.hwproj.mafiagame.phases.PhaseFragment;
import me.hwproj.mafiagame.phases.PlayerAction;

class WaitClient implements GamePhaseClient {
    @Override
    public PhaseFragment createFragment(Client client) {
        return new WaitFragment(client);
    }

    @Override
    public GameState deserializeGameState(DataInputStream dataStream) throws DeserializationException {
        return new WaitState();
    }

    @Override
    public void serializeAction(DataOutputStream dataOutput, PlayerAction action) throws SerializationException {
        throw new SerializationException("Doesn't send any actions");
    }
}
