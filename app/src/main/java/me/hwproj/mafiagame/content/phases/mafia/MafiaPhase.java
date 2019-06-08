package me.hwproj.mafiagame.content.phases.mafia;

import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.phase.GamePhase;
import me.hwproj.mafiagame.phase.GamePhaseClient;
import me.hwproj.mafiagame.phase.GamePhaseServer;

public class MafiaPhase implements GamePhase {
    @Override
    public GamePhaseServer getServerPhase(Server server) {
        return new MafiaServer(server);
    }

    @Override
    public GamePhaseClient getClientPhase() {
        return new MafiaClient();
    }
}
