package me.hwproj.mafiagame.gameflow;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.GamePhaseSerializer;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phase.GamePhase;

/**
 * A description of the game before it started.
 * Used to initialize server and clients.
 */
public class Settings {
    public List<GamePhase> phases; // should be public
    private final List<PlayerSettings> playerSettings;

    public Settings(List<GamePhase> phases, List<PlayerSettings> players) {
        this.phases = phases;
        playerSettings = players;
    }

    public List<PlayerSettings> getPlayerSettings() {
        return playerSettings;
    }

    public static Settings deserialize(DataInputStream stream) throws DeserializationException {
        try {
            int phaseCount = stream.readInt();
            int playerCount = stream.readInt();

            List<GamePhase> phases = new ArrayList<>();
            for (int i = 0; i < phaseCount; i++) {
                phases.add(GamePhaseSerializer.deserialize(stream));
            }
            List<PlayerSettings> players = new ArrayList<>();
            for (int i = 0; i < playerCount; i++) {
                players.add(PlayerSettings.deserialize(stream));
            }
            return new Settings(phases, players);
        } catch (IOException e) {
            throw new DeserializationException(e);
        }
    }

    public void serialize(DataOutputStream stream) throws SerializationException {
        try {
            stream.writeInt(phases.size());
            stream.writeInt(playerSettings.size());

            for (GamePhase phase : phases) {
                stream.write(GamePhaseSerializer.serialize(phase.getClass()));
            }
            for (PlayerSettings ps : playerSettings) {
                ps.serialize(stream);
            }

        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }
}
