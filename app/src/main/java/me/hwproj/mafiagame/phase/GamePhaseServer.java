package me.hwproj.mafiagame.phase;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;

/**
 * Complete description of server-side phase logic
 */
public interface GamePhaseServer {
    /**
     * Handles received PlayerAction
     * Note that this action might be from another phase.
     * @param action action to handle
     */
    void processPlayerAction(PlayerAction action);

    /**
     * This method is called every time the phase starts
     */
    void initPhase();

    /**
     * This method is called every time the phase ends
     */
    void onEnd();

    /**
     * How to serialize a game state sent by this phase.
     *
     * Actually if a phase tries to send a GameState after it starts next phase,
     * that GameState will be serialized using next phase's serialization. So it is possible
     * to get other phase's GameState here. But currently all phases do not do this.
     *
     * Anyway it is recommended to check if provided GameState is of correct type and
     * throw a SerializationException if not.
     *
     * @param state state sent by this phase's client
     */
    void serializeGameState(DataOutputStream dataOut, GameState state) throws SerializationException;

     /**
      * How to deserialize a game action received from a client.
      * Player action is guaranteed to be sent by a client-side part of the same phase
      * @param dataStream stream to read message from
      * @return deserialized player action
      * @throws DeserializationException if deserialization fails
     */
    PlayerAction deserialize(DataInputStream dataStream) throws DeserializationException;
}
