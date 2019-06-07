package me.hwproj.mafiagame.content.phases.investigator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phase.GamePhaseClient;
import me.hwproj.mafiagame.phase.GameState;
import me.hwproj.mafiagame.phase.PhaseFragment;
import me.hwproj.mafiagame.phase.PlayerAction;

class InvClient implements GamePhaseClient {
    @Override
    public PhaseFragment createFragment(Client client) {
        return new InvFragment(client);
    }

    @Override
    public GameState deserializeGameState(DataInputStream dataStream) throws DeserializationException {
        try {
            return InvState.deserialize(dataStream);
        } catch (IOException e) {
            throw new DeserializationException(e);
        }
    }

    @Override
    public void serializeAction(DataOutputStream dataOutput, PlayerAction action) throws SerializationException {
        if (!(action instanceof InvAction)) {
            throw new SerializationException("wrong action");
        }
        ((InvAction) action).getPick().serialize(dataOutput);
    }

    @Override
    public String toolbarText() {
        return "Investigator's turn";
    }
}
