package me.hwproj.mafiagame.phases;

import java.io.DataInputStream;

import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;

public interface GamePhaseClient {
    /**
     * Returns a fragment corresponding to this phase.
     */
    PhaseFragment createFragment(Client client);

    /**
     * Guaranteed to be sent by this phase's client
     */
    GameState deserializeGameState(DataInputStream dataStream) throws DeserializationException;
}
