package me.hwproj.mafiagame.content.phases.vote;

import android.util.Log;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phases.GamePhaseServer;
import me.hwproj.mafiagame.phases.GameState;
import me.hwproj.mafiagame.phases.PlayerAction;

public class VotePhaseServer implements GamePhaseServer {
    private int[] playersChoices;
    private boolean[] choiceFixed;
    private boolean[] cantChoose;
    private int fixedCount;
    private Server serv;

    public VotePhaseServer(Server server) {
        serv = server;
    }

    @Override
    public void initPhase() {
        int playerCount = serv.currentGameData.players.size();
        playersChoices = new int[playerCount];
        Arrays.fill(playersChoices, -1);
        choiceFixed = new boolean[playerCount];
        cantChoose = new boolean[playerCount];
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

        if (cantChoose[castedAction.chosenPlayerNumber]) {
            return; // probably old
        }

        playersChoices[p] = castedAction.chosenPlayerNumber;

        // update choiceFixed[p] and fixedCount
        updateFixed(p, castedAction.fixed);

        if (fixedCount == 1) {
//        if (fixedCount == serv.playerAliveCount()) { TODO DEBUG replace if condition
            Log.d("qwe", Arrays.toString(playersChoices));
            int[] chosenBy = new int[serv.playerCount()];
            for (int i = 0; i < serv.playerCount(); i++) {
                if (playersChoices[i] != -1) {
                    chosenBy[playersChoices[i]]++;
                }
            }

            int maxVotes = -1;
            List<Integer> maxChosen = new ArrayList<>();
            for (int i = 0; i < serv.playerCount(); i++) {
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
                serv.currentGameData.players.get(s.killedPlayer).dead = true;
                Log.d("qwe", "processPlayerAction: killed " + s.killedPlayer);
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
}
