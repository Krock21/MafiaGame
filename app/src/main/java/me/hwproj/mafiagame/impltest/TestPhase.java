package me.hwproj.mafiagame.impltest;

import android.os.Handler;

import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.phases.GamePhase;
import me.hwproj.mafiagame.phases.GamePhaseClient;
import me.hwproj.mafiagame.phases.GamePhaseServer;

public class TestPhase extends GamePhase {

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
