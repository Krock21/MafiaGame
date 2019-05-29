package me.hwproj.mafiagame.content.phases.wait;

import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.phases.GamePhase;
import me.hwproj.mafiagame.phases.GamePhaseClient;
import me.hwproj.mafiagame.phases.GamePhaseServer;

public class WaitPhase extends GamePhase {
    @Override
    public GamePhaseServer getServerPhase(Server server) {
        return new WaitServer(server);
    }

    @Override
    public GamePhaseClient getClientPhase() {
        return new WaitClient();
    }
}
