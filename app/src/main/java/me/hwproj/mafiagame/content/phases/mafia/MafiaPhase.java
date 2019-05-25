package me.hwproj.mafiagame.content.phases.mafia;

import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.phases.GamePhase;
import me.hwproj.mafiagame.phases.GamePhaseClient;
import me.hwproj.mafiagame.phases.GamePhaseServer;

public class MafiaPhase extends GamePhase {
    @Override
    public GamePhaseServer getServerPhase(Server server) {
        return new MafiaServer(server);
    }

    @Override
    public GamePhaseClient getClientPhase() {
        return new MafiaClient();
    }
}
