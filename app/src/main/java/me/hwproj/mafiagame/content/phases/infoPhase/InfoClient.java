package me.hwproj.mafiagame.content.phases.infoPhase;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phases.GamePhaseClient;
import me.hwproj.mafiagame.phases.GameState;
import me.hwproj.mafiagame.phases.PhaseFragment;
import me.hwproj.mafiagame.phases.PlayerAction;

public class InfoClient implements GamePhaseClient {
    @Override
    public PhaseFragment createFragment(Client client) {
        return new InfoFragment(client);
    }

    @Override
    public GameState deserializeGameState(DataInputStream dataStream) throws DeserializationException {
        return InfoState.deserialize(dataStream);
    }

    @Override
    public void serializeAction(DataOutputStream dataOutput, PlayerAction action) throws SerializationException {
        if (!(action instanceof InfoAction)) {
            throw new SerializationException("wrong");
        }
        try {
            dataOutput.writeBoolean(((InfoAction) action).wantsNext);
            dataOutput.writeInt(((InfoAction) action).playerNumber);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }
}
