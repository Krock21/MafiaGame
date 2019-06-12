package me.hwproj.mafiagame.content.phases.vote;

import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.phase.GamePhase;
import me.hwproj.mafiagame.phase.GamePhaseClient;
import me.hwproj.mafiagame.phase.GamePhaseServer;

public class VotePhase implements GamePhase {
    @Override
    public GamePhaseServer getServerPhase(Server server) {
        return new VotePhaseServer(server);
    }

    @Override
    public GamePhaseClient getClientPhase() {
        return new VotePhaseClient();
    }
}
