package me.hwproj.mafiagame.content.phases.wait;

import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.phase.GamePhase;
import me.hwproj.mafiagame.phase.GamePhaseClient;
import me.hwproj.mafiagame.phase.GamePhaseServer;

public class WaitPhase implements GamePhase {
    @Override
    public GamePhaseServer getServerPhase(Server server) {
        return new WaitServer(server);
    }

    @Override
    public GamePhaseClient getClientPhase() {
        return new WaitClient();
    }
}
