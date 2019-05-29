package me.hwproj.mafiagame.content.phases.investigator;

import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.phases.GamePhase;
import me.hwproj.mafiagame.phases.GamePhaseClient;
import me.hwproj.mafiagame.phases.GamePhaseServer;

public class InvPhase extends GamePhase {
    @Override
    public GamePhaseServer getServerPhase(Server server) {
        return new InvServer(server);
    }

    @Override
    public GamePhaseClient getClientPhase() {
        return new InvClient();
    }
}
