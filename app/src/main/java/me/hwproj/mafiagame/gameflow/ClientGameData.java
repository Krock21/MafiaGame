package me.hwproj.mafiagame.gameflow;

import java.util.ArrayList;
import java.util.List;

import me.hwproj.mafiagame.networking.FullGameState;
import me.hwproj.mafiagame.phase.GamePhaseClient;

/**
 * Stores all information about the game that client
 * devices know.
 */
public class ClientGameData {
    public final List<Player> players = new ArrayList<>();
    public final List<GamePhaseClient> phases = new ArrayList<>();
    private GamePhaseClient currentPhase;
    private int currentPhaseNumber = -1;

    ClientGameData() {
    }

    public int getCurrentPhaseNumber() {
        return currentPhaseNumber;
    }

    public GamePhaseClient getCurrentPhase() {
        return currentPhase;
    }

    /**
     * Changes current phase in GameData
     */
    void nextPhase() {
        currentPhaseNumber++;
        currentPhase = phases.get(currentPhaseNumber % phases.size());
    }

    /**
     * Applies information about players received from server.
     * Does not change stored current phase
     * @param state state of the game to get information from
     */
    void update(FullGameState state) {
        for (int i = 0; i < players.size(); i++) {
            players.get(i).dead = state.getIsDead()[i];
        }
    }
}
