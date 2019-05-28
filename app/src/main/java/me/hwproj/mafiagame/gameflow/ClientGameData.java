package me.hwproj.mafiagame.gameflow;

import java.util.ArrayList;
import java.util.List;

import me.hwproj.mafiagame.gameflow.Player;
import me.hwproj.mafiagame.networking.FullGameState;
import me.hwproj.mafiagame.phases.GamePhase;
import me.hwproj.mafiagame.phases.GamePhaseClient;

public class ClientGameData {
    // TODO make everything private
    public List<Player> players = new ArrayList<>();
    public List<GamePhaseClient> phases = new ArrayList<>();
    public GamePhaseClient currentPhase;
    public int phaseNumber = 0;



    public ClientGameData() {
    }

    public void nextPhase() {
        if (currentPhase == null) {
            currentPhase = phases.get(0);
        } else {
            phaseNumber++;
            currentPhase = phases.get(phaseNumber % phases.size());
        }
    }

    public void update(FullGameState state) {
        for (int i = 0; i < players.size(); i++) {
            players.get(i).dead = state.getIsDead()[i];
        }
    }
}
