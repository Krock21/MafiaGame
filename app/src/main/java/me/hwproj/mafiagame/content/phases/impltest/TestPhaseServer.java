package me.hwproj.mafiagame.content.phases.impltest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phase.GamePhaseServer;
import me.hwproj.mafiagame.phase.GameState;
import me.hwproj.mafiagame.phase.PlayerAction;

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

        sum += castedAction.getAdded();
        server.sendGameState(new TestPhaseGameState(sum));

        if (castedAction.isNext()) {
            server.startNextPhase();
        }
    }

    @Override
    public void initPhase() {

    }

    @Override
    public void onEnd() {
        sum = sum % 1000000;
        sum *= 1000;
    }

    @Override
    public void serializeGameState(DataOutputStream dataOut, GameState state) throws SerializationException {
        if (!(state instanceof TestPhaseGameState)) {
            throw new SerializationException("Wrong state");
        }

        TestPhaseGameState s = (TestPhaseGameState) state;
        try {
            dataOut.writeInt(s.getSum());
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public PlayerAction deserialize(DataInputStream dataStream) throws DeserializationException {
        try {
            boolean b = dataStream.readBoolean();
            if (b) {
                return TestPhasePlayerAction.nextPhase();
            }
            return new TestPhasePlayerAction(dataStream.readInt());
        } catch (IOException e) {
            throw new DeserializationException(e);
        }
    }
}
