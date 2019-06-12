package me.hwproj.mafiagame.gameflow;

import java.util.ArrayList;
import java.util.List;

import me.hwproj.mafiagame.phase.GamePhaseServer;

/**
 * Holds full game state.
 */
public class ServerGameData {
    public final List<Player> players = new ArrayList<>();
    public final List<GamePhaseServer> phases = new ArrayList<>();
    private GamePhaseServer currentPhase;
    private int phaseNumber = -1;

    public final List<String> infoToDisplay = new ArrayList<>();

    public GamePhaseServer getCurrentPhase() {
        return currentPhase;
    }

    public int getCurrentPhaseNumber() {
        return phaseNumber;
    }

    public int playerAliveCount() {
        int count = 0;
        for (Player p : players) {
            if (!p.dead) {
                count++;
            }
        }
        return count;
    }

    public int playerCount() {
        return players.size();
    }

    // ---------------- for server ----------------

    void endThisPhase() {
        currentPhase = null;
    }

    void startNextPhase() {
        phaseNumber++;
        if (phaseNumber % phases.size() == 0) {
            applyEffects();
        }
        currentPhase = phases.get(phaseNumber % phases.size());
        currentPhase.initPhase();
    }

    private void applyEffects() {
        for (Player p : players) {
            p.resolveEffects(this);
        }
    }
}
