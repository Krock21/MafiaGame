package me.hwproj.mafiagame.phases;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;

public interface GamePhaseClient {
    /**
     * Returns a fragment corresponding to this phase.
     */
    PhaseFragment createFragment(Client client);

    /**
     * Guaranteed to be sent by this phase's client
     */
    GameState deserializeGameState(DataInputStream dataStream) throws DeserializationException;

    void serializeAction(DataOutputStream dataOutput, PlayerAction action) throws SerializationException;

    String toolbarText();
}
