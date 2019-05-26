package me.hwproj.mafiagame.content.phases.mafia;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import me.hwproj.mafiagame.content.phases.abstractpick.PickState;
import me.hwproj.mafiagame.content.phases.doctor.DoctorAction;
import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phases.GamePhaseClient;
import me.hwproj.mafiagame.phases.GameState;
import me.hwproj.mafiagame.phases.PhaseFragment;
import me.hwproj.mafiagame.phases.PlayerAction;

public class MafiaClient implements GamePhaseClient {
    @Override
    public PhaseFragment createFragment(Client client) {
        return new MafiaFragment(client);
    }

    @Override
    public GameState deserializeGameState(DataInputStream dataStream) throws DeserializationException {
        return new MafiaState(PickState.deserialize(dataStream));
    }

    @Override
    public void serializeAction(DataOutputStream dataOutput, PlayerAction action) throws SerializationException {
        if (!(action instanceof MafiaAction)) {
            throw new SerializationException("wrong state");
        }
        ((MafiaAction) action).getPickAction().serialize(dataOutput);
    }
}
