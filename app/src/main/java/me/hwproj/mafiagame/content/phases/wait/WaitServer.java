package me.hwproj.mafiagame.content.phases.wait;

import android.os.Handler;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phase.GamePhaseServer;
import me.hwproj.mafiagame.phase.GameState;
import me.hwproj.mafiagame.phase.PlayerAction;

class WaitServer implements GamePhaseServer {

    private static final long WAIT_TIME = 5 * 1000; // ms
    private final Server serv;
    private Handler handlerToPostTo = new Handler();

    public WaitServer(Server serv) {
        this.serv = serv;
    }

    @Override
    public void processPlayerAction(PlayerAction action) {
        // don't receive actions
    }

    @Override
    public void initPhase() {
        handlerToPostTo.postDelayed(serv::startNextPhase, WAIT_TIME);
    }

    @Override
    public void onEnd() {

    }

    @Override
    public void serializeGameState(DataOutputStream dataOut, GameState state) throws SerializationException {
        if (!(state instanceof WaitState)) {
            throw new SerializationException("wrong");
        }
        // state doesn't have any fields
    }

    @Override
    public PlayerAction deserialize(DataInputStream dataStream) throws DeserializationException {
        throw new DeserializationException("shouldn't receive any Actions");
    }
}
