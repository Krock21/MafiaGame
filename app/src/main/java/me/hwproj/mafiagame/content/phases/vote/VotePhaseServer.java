package me.hwproj.mafiagame.content.phases.vote;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phase.GamePhaseServer;
import me.hwproj.mafiagame.phase.GameState;
import me.hwproj.mafiagame.phase.PlayerAction;

public class VotePhaseServer implements GamePhaseServer {
    private int[] playersChoices;
    private boolean[] choiceFixed;
    private boolean[] cantChoose;
    private int fixedCount;
    private final Server serv;

    public VotePhaseServer(Server server) {
        serv = server;
    }

    @Override
    public void initPhase() {
        int playerCount = serv.getGameData().players.size();
        playersChoices = new int[playerCount];
        Arrays.fill(playersChoices, -1);
        choiceFixed = new boolean[playerCount];
        cantChoose = new boolean[playerCount + 1];
        fixedCount = 0;
    }

    @Override
    public void processPlayerAction(PlayerAction action) {
        if (!(action instanceof VotePhasePlayerAction)) {
            Log.d("Bug", "action of class " + action.getClass().toString() + " in Vote server");
            return;
        }
        VotePhasePlayerAction castedAction = (VotePhasePlayerAction) action;
        int p = castedAction.thisPlayer;

        if (castedAction.chosenPlayerNumber > 0 && cantChoose[castedAction.chosenPlayerNumber]) {
            return; // probably old
        }

        playersChoices[p] = castedAction.chosenPlayerNumber;

        if (castedAction.chosenPlayerNumber < 0) {
            castedAction.fixed = false; // cant fix on not choosing
        }

        // update choiceFixed[p] and fixedCount
        updateFixed(p, castedAction.fixed);

        if (fixedCount == serv.getGameData().playerAliveCount()) { //
            Log.d("qwe", Arrays.toString(playersChoices));

            int[] chosenBy = new int[serv.getGameData().playerCount() + 1]; // +1 for peace
            for (int i = 0; i < serv.getGameData().playerCount(); i++) {
                if (playersChoices[i] != -1) {
                    chosenBy[playersChoices[i]]++;
                }
            }

            int maxVotes = -1;
            List<Integer> maxChosen = new ArrayList<>();
            for (int i = 0; i < chosenBy.length; i++) {
                if (chosenBy[i] > maxVotes) {
                    maxVotes = chosenBy[i];
                    maxChosen.clear();
                }
                if (chosenBy[i] == maxVotes) {
                    maxChosen.add(i);
                }
            }

            if (maxChosen.size() == 1) {
                VotePhaseGameState s = new VotePhaseGameState();
                s.end = true;
                s.killedPlayer = maxChosen.get(0);
                if (s.killedPlayer != serv.getGameData().playerCount()) {
                    serv.getGameData().players.get(s.killedPlayer).dead = true;
                    Log.d("qwe", "processPlayerAction: killed " + s.killedPlayer);
                }
                serv.sendGameState(s);
                serv.startNextPhase();
            } else {
                Arrays.fill(cantChoose, true);
                for (int pl : maxChosen) {
                    cantChoose[pl] = false;
                }
                VotePhaseGameState s = new VotePhaseGameState();
                s.end = false;
                s.cantChoose = cantChoose;
                serv.sendGameState(s);
            }
        }
    }

    private void updateFixed(int p, boolean fixed) {
        if (choiceFixed[p]) {
            fixedCount--;
        }
        if (fixed) {
            fixedCount++;
        }
        choiceFixed[p] = fixed;
    }

    @Override
    public void onEnd() {

    }

    @Override
    public void serializeGameState(DataOutputStream dataOut, GameState state) throws SerializationException {
        if (!(state instanceof VotePhaseGameState)) {
            throw new SerializationException("wrong state");
        }
        ((VotePhaseGameState) state).serialize(dataOut);
    }

    @Override
    public PlayerAction deserialize(DataInputStream dataStream) throws DeserializationException {
        return VotePhasePlayerAction.deserialize(dataStream);
    }
}
