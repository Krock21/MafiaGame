package me.hwproj.mafiagame.phase;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;

/**
 * A part of client-side phase logic
 */
public interface GamePhaseClient {
    /**
     * Returns a fragment corresponding to this phase.
     */
    PhaseFragment createFragment(Client client);

    /**
     * Deserialize a GameState sent by a client-side half of this phase.
     * Guaranteed to be sent by this phase's client.
     */
    GameState deserializeGameState(DataInputStream dataStream) throws DeserializationException;

    /**
     * Serialize a PlayerAction sent by this phase
     * @param dataOutput stream to put serialization to
     * @param action     action to serialize
     * @throws SerializationException if serialization fails
     */
    void serializeAction(DataOutputStream dataOutput, PlayerAction action) throws SerializationException;

    /**
     * What to show on toolbar during this phase.
     * This method is called every time the phase starts
     * @return text to show on toolbar
     */
    String toolbarText();
}
