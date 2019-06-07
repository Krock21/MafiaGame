package me.hwproj.mafiagame.phase;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;

public interface GamePhaseServer {
    void processPlayerAction(PlayerAction action);

    void initPhase();

    void onEnd();

    /**
     * @param state guaranteed to be sent by this phase's client
     */
    void serializeGameState(DataOutputStream dataOut, GameState state) throws SerializationException;

    PlayerAction deserialize(DataInputStream dataStream) throws DeserializationException;
}
