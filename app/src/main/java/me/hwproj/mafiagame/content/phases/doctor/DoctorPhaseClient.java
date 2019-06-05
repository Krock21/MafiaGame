package me.hwproj.mafiagame.content.phases.doctor;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import me.hwproj.mafiagame.content.phases.abstractpick.PickState;
import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phases.GamePhaseClient;
import me.hwproj.mafiagame.phases.GameState;
import me.hwproj.mafiagame.phases.PhaseFragment;
import me.hwproj.mafiagame.phases.PlayerAction;
import me.hwproj.mafiagame.util.ModifiableBoolean;

class DoctorPhaseClient implements GamePhaseClient {
    @Override
    public PhaseFragment createFragment(Client client) {
        return new DoctorFragment(client, pickedSelfLastTime);
    }

    @Override
    public GameState deserializeGameState(DataInputStream dataStream) throws DeserializationException {
        return new DoctorState(PickState.deserialize(dataStream));
    }

    @Override
    public void serializeAction(DataOutputStream dataOutput, PlayerAction action) throws SerializationException {
        if (!(action instanceof DoctorAction)) {
            throw new SerializationException("wrong state");
        }
        ((DoctorAction) action).getPickAction().serialize(dataOutput);
    }

    @Override
    public String toolbarText() {
        return "Doctor's turn";
    }

    private ModifiableBoolean pickedSelfLastTime = new ModifiableBoolean(false);
}
