package me.hwproj.mafiagame.content.phases.abstractpick;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.hwproj.mafiagame.gameflow.Player;
import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.gameplay.Role;
import me.hwproj.mafiagame.phases.GamePhaseServer;

public abstract class PickServer implements GamePhaseServer {

    private int[] playersChoices;
    private boolean[] choiceFixed;
    private int fixedCount;
    private int pickersCount;
    protected final Server serv;
    private Role pickersRole;

    protected PickServer(Server serv, Role pickersRole) {
        this.serv = serv;
        this.pickersRole = pickersRole;
    }

    protected void processPickAction(PickAction action) {
        playersChoices[action.playerNumber] = action.pick;
        updateFixed(action.playerNumber, action.isFixed);

        PickState data = new PickState();
        data.end = false;
        data.picks = Arrays.copyOf(playersChoices, playersChoices.length);
        sendPickState(data);

        if (fixedCount == pickersCount) {
            int pickedPlayer = playersChoices[0];
            boolean pickedByAll = true;
            for (int choice : playersChoices) {
                if (choice != pickedPlayer) {
                    pickedByAll = false;
                }
            }

            if (pickedByAll) {
                PickState finalData = new PickState();
                finalData.end = true;
                finalData.pickedPlayer = pickedPlayer;
                sendPickState(finalData);
                Log.d("pick", "server: send end");
                onPickComplete(pickedPlayer);
            }
        }
    }

    protected abstract void onPickComplete(int pickedPlayer);

    protected abstract void sendPickState(PickState data);

    @Override
    public void initPhase() {
        List<Integer> thisRoleIds = new ArrayList<>();
        for (int i = 0; i < serv.playerCount(); i++) {
            Player p = serv.currentGameData.players.get(i);
            if (p.role == pickersRole && !p.dead) {
                thisRoleIds.add(i);
            }
        }

        pickersCount = thisRoleIds.size();

        if (pickersCount == 0) {
            // TODO make it wait, don't start next phase immediately
            serv.startNextPhase();
        }

        playersChoices = new int[pickersCount];
        Arrays.fill(playersChoices, -1);
        choiceFixed = new boolean[pickersCount];
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
}
