package me.hwproj.mafiagame.phases;

import me.hwproj.mafiagame.gameflow.Server;

public interface GamePhaseServer {
    void processPlayerAction(PlayerAction action, Server serv);

    void onEnd();
}
