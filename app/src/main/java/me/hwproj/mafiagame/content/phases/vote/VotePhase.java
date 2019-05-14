package me.hwproj.mafiagame.content.phases.vote;

import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.phases.GamePhase;
import me.hwproj.mafiagame.phases.GamePhaseClient;
import me.hwproj.mafiagame.phases.GamePhaseServer;

public class VotePhase extends GamePhase {
    @Override
    public GamePhaseServer getServerPhase(Server server) {
        return new VotePhaseServer(server);
    }

    @Override
    public GamePhaseClient getClientPhase() {
        return new VotePhaseClient();
    }
}
