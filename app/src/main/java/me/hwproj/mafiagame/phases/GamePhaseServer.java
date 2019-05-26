package me.hwproj.mafiagame.phases;

import java.io.DataOutputStream;

import me.hwproj.mafiagame.networking.serialization.SerializationException;

public interface GamePhaseServer {
    void processPlayerAction(PlayerAction action);

    void initPhase();

    void onEnd();

    /**
     * @param state guaranteed to be sent by this phase's client
     */
    void serializeGameState(DataOutputStream dataOut, GameState state) throws SerializationException;
}
