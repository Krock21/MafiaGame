package me.hwproj.mafiagame.impltest;

import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.phases.GamePhaseServer;
import me.hwproj.mafiagame.phases.PlayerAction;

public class TestPhaseServer implements GamePhaseServer {
    private int sum = 0;
    private Server server;

    public TestPhaseServer(Server server) {
        this.server = server;
    }

    @Override
    public void processPlayerAction(PlayerAction action) {
        if (!(action instanceof TestPhasePlayerAction)) {
            return;
        }

        TestPhasePlayerAction castedAction = (TestPhasePlayerAction) action;

        if (castedAction.isNext()) {
            server.startNextPhase();
        }

        sum += castedAction.getAdded();
        server.sendGameState(new TestPhaseGameState(sum));
    }

    @Override
    public void onEnd() {
        sum = sum % 1000000;
        sum *= 1000;
    }
}
