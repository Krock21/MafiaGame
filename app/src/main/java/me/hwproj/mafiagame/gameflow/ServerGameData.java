package me.hwproj.mafiagame.gameflow;

import java.util.ArrayList;
import java.util.List;

import me.hwproj.mafiagame.phases.GamePhaseClient;
import me.hwproj.mafiagame.phases.GamePhaseServer;

public class ServerGameData {
    // TODO make everything private
    public List<Player> players = new ArrayList<>();
    public List<GamePhaseServer> phases = new ArrayList<>();
    public GamePhaseServer currentPhase;
    public int phaseNumber = 0;



    public ServerGameData() {
    }

    public void nextPhase() {
        if (currentPhase == null) {
            currentPhase = phases.get(0);
        } else {
            phaseNumber++;
            currentPhase = phases.get(phaseNumber % phases.size());
        }
    }
}
