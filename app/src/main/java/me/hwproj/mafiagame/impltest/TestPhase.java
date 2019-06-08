package me.hwproj.mafiagame.impltest;

import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.phase.GamePhase;
import me.hwproj.mafiagame.phase.GamePhaseClient;
import me.hwproj.mafiagame.phase.GamePhaseServer;

public class TestPhase implements GamePhase {

    public static final String PHASE_NAME = "GamePhase";

    @Override
    public GamePhaseServer getServerPhase(Server server) {
        return new TestPhaseServer(server);
    }

    @Override
    public GamePhaseClient getClientPhase() {
        return new TestPhaseClient();
    }
}
