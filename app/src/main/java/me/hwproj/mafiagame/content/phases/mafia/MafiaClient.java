package me.hwproj.mafiagame.content.phases.mafia;

import java.io.DataInputStream;

import me.hwproj.mafiagame.content.phases.abstractpick.PickState;
import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.phases.GamePhaseClient;
import me.hwproj.mafiagame.phases.GameState;
import me.hwproj.mafiagame.phases.PhaseFragment;

public class MafiaClient implements GamePhaseClient {
    @Override
    public PhaseFragment createFragment(Client client) {
        return new MafiaFragment(client);
    }

    @Override
    public GameState deserializeGameState(DataInputStream dataStream) throws DeserializationException {
        return new MafiaState(PickState.deserialize(dataStream));
    }
}
