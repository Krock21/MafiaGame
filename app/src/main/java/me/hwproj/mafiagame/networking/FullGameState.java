package me.hwproj.mafiagame.networking;

import me.hwproj.mafiagame.gameflow.ServerGameData;
import me.hwproj.mafiagame.phases.GameState;

public class FullGameState {
    private boolean isDead[];
    private GameState phaseState;

    public FullGameState(ServerGameData data, GameState phaseState) {
        isDead = new boolean[data.players.size()];
        for (int i = 0; i < data.players.size(); i++) {
            isDead[i] = data.players.get(i).dead;
        }
        this.phaseState = phaseState;
    }

    public boolean[] getIsDead() {
        return isDead;
    }

    public GameState getPhaseState() {
        return phaseState;
    }
}
