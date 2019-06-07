package me.hwproj.mafiagame.gameflow;

import java.util.ArrayList;
import java.util.List;

import me.hwproj.mafiagame.phase.GamePhaseServer;

public class ServerGameData {
    // TODO make everything private
    public List<Player> players = new ArrayList<>();
    public List<GamePhaseServer> phases = new ArrayList<>();
    public GamePhaseServer currentPhase;
    public int phaseNumber = -1;

    public final List<String> infoToDisplay = new ArrayList<>();

    public ServerGameData() {
    }

    public void endThisPhase() {
        currentPhase = null;
    }

    public void startNextPhase() {
        phaseNumber++;
        if (phaseNumber % phases.size() == 0) { // TODO add night results here
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
