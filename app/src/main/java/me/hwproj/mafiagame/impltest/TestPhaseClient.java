package me.hwproj.mafiagame.impltest;

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

    @Override
    public void serializeAction(DataOutputStream dataOutput, PlayerAction action) throws SerializationException {
        if (!(action instanceof TestPhasePlayerAction)) {
            throw new SerializationException("wrong action");
        }
        try {
            if (((TestPhasePlayerAction) action).isNext()) {
                dataOutput.writeBoolean(true);
            } else {
                dataOutput.writeBoolean(false);
                dataOutput.writeInt(((TestPhasePlayerAction) action).getAdded());
            }
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public String toolbarText() {
        return "Test phase";
    }
}
