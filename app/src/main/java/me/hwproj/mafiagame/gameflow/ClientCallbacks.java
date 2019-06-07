package me.hwproj.mafiagame.gameflow;

import me.hwproj.mafiagame.phase.GameState;

public interface ClientCallbacks {
    void handleGameState(GameState state);
    void finishGame(String message);
    void setToolbarText(String text);
}
