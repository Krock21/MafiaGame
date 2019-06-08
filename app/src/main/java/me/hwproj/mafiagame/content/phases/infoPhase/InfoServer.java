package me.hwproj.mafiagame.content.phases.infoPhase;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phase.GamePhaseServer;
import me.hwproj.mafiagame.phase.GameState;
import me.hwproj.mafiagame.phase.PlayerAction;

public class InfoServer implements GamePhaseServer {
    private final Server server;
    private int finishedCount;
    private boolean[] finished;
    private ArrayList<String> info;

    public InfoServer(Server server) {
        this.server = server;
    }

    @Override
    public void processPlayerAction(PlayerAction action) {
        if (!(action instanceof InfoAction)) {
            Log.d("qwe", "info server: wrong action");
            return;
        }
        InfoAction a = (InfoAction) action;
        if (finished[a.playerNumber]) {
            finishedCount--;
        }
        if (a.wantsNext) {
            finishedCount++;
        }
        finished[a.playerNumber] = a.wantsNext;

        if (finishedCount >= server.getGameData().playerAliveCount()) {
            server.startNextPhase();
        }

        server.sendGameState(new InfoState(info));
    }

    @Override
    public void initPhase() {
        info = new ArrayList<>(server.getInfo());
        finishedCount = 0;
        finished = new boolean[server.getGameData().playerCount()];

        server.clearInfo();
        server.sendGameState(new InfoState(info));
    }

    @Override
    public void onEnd() {

    }

    @Override
    public void serializeGameState(DataOutputStream dataOut, GameState state) throws SerializationException {
        if (!(state instanceof InfoState)) {
            return;
        }
        ((InfoState) state).serialize(dataOut);
    }

    @Override
    public PlayerAction deserialize(DataInputStream dataStream) throws DeserializationException {
        try {
            boolean wantsNext = dataStream.readBoolean();
            int playerNumber = dataStream.readInt();
            return new InfoAction(wantsNext, playerNumber);
        } catch (IOException e) {
            throw new DeserializationException(e);
        }
    }
}
