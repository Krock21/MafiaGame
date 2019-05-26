package me.hwproj.mafiagame.content.phases.vote;

import java.io.DataInputStream;

import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.phases.GamePhaseClient;
import me.hwproj.mafiagame.phases.GameState;
import me.hwproj.mafiagame.phases.PhaseFragment;

public class VotePhaseClient implements GamePhaseClient {
    @Override
    public PhaseFragment createFragment(Client client) {
        return new VotePhaseFragment(client);
    }

    @Override
    public GameState deserializeGameState(DataInputStream dataStream) throws DeserializationException {
        return VotePhaseGameState.deserialize(dataStream);
    }
}
