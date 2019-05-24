package me.hwproj.mafiagame.phases;

import me.hwproj.mafiagame.gameflow.Server;

public interface GamePhaseServer {
    void processPlayerAction(PlayerAction action);

    void initPhase();

    void onEnd();
}
