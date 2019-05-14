package me.hwproj.mafiagame.content.phases.vote;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.phases.GamePhaseServer;
import me.hwproj.mafiagame.phases.PlayerAction;

class VotePhaseServer implements GamePhaseServer {
    private int[] playersChoices;
    private boolean[] choiseFixed;
    private boolean[] cantChoose;
    private int fixedCount;
    private Server serv;

    public VotePhaseServer(Server server) {
        int playerCount = server.currentGameData.players.size();
        playersChoices = new int[playerCount];
        choiseFixed = new boolean[playerCount];
        cantChoose = new boolean[playerCount];
        fixedCount = 0;
        serv = server;
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

        if (choiseFixed[p]) {
            fixedCount--;
        }
        if (castedAction.fixed) {
            fixedCount++;
        }
        choiseFixed[p] = castedAction.fixed;

        if (fixedCount == serv.playerAliveCount()) {
            int[] chosenBy = new int[serv.playerCount()];
            for (int i = 0; i < serv.playerCount(); i++) {
                chosenBy[playersChoices[i]]++;
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
                serv.sendGameState(s);
                serv.startNextPhase();
            }

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

    @Override
    public void onEnd() {

    }
}
