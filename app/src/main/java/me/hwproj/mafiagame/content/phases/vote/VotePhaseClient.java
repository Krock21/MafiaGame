package me.hwproj.mafiagame.content.phases.vote;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phases.GamePhaseClient;
import me.hwproj.mafiagame.phases.GameState;
import me.hwproj.mafiagame.phases.PhaseFragment;
import me.hwproj.mafiagame.phases.PlayerAction;

public class VotePhaseClient implements GamePhaseClient {
    @Override
    public PhaseFragment createFragment(Client client) {
        return new VotePhaseFragment(client);
    }

    @Override
    public GameState deserializeGameState(DataInputStream dataStream) throws DeserializationException {
        return VotePhaseGameState.deserialize(dataStream);
    }

    @Override
    public void serializeAction(DataOutputStream dataOutput, PlayerAction action) throws SerializationException {
        if (!(action instanceof VotePhasePlayerAction)) {
            throw new SerializationException("wrong state");
        }
        ((VotePhasePlayerAction) action).serialize(dataOutput);
    }
}
