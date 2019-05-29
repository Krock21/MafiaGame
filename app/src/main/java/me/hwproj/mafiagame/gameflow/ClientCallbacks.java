package me.hwproj.mafiagame.gameflow;

import me.hwproj.mafiagame.phases.GameState;

public interface ClientCallbacks {
    void handleGameState(GameState state);
    void finishGame(String message);
}
