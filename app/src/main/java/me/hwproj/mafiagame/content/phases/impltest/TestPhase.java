package me.hwproj.mafiagame.content.phases.impltest;

import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.phase.GamePhase;
import me.hwproj.mafiagame.phase.GamePhaseClient;
import me.hwproj.mafiagame.phase.GamePhaseServer;

/**
 * A test phase that demonstrates that the game and networking works.
 * Currently unused.
 */
public class TestPhase implements GamePhase {
 @Override
    public GamePhaseServer getServerPhase(Server server) {
        return new TestPhaseServer(server);
    }

    @Override
    public GamePhaseClient getClientPhase() {
        return new TestPhaseClient();
    }
}
