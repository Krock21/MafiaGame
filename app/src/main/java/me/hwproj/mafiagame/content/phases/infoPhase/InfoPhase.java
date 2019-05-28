package me.hwproj.mafiagame.content.phases.infoPhase;

import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.phases.GamePhase;
import me.hwproj.mafiagame.phases.GamePhaseClient;
import me.hwproj.mafiagame.phases.GamePhaseServer;

public class InfoPhase extends GamePhase {
    @Override
    public GamePhaseServer getServerPhase(Server server) {
        return new InfoServer(server);
    }

    @Override
    public GamePhaseClient getClientPhase() {
        return new InfoClient();
    }
}
