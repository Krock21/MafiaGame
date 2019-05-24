package me.hwproj.mafiagame.content.phases.vote;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import org.jetbrains.annotations.NotNull;

import me.hwproj.mafiagame.GameCreate;
import me.hwproj.mafiagame.R;
import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.impltest.TestPhasePlayerAction;
import me.hwproj.mafiagame.phases.GameState;
import me.hwproj.mafiagame.phases.PhaseFragment;
import me.hwproj.mafiagame.util.table.TablePick;

class VotePhaseFragment extends PhaseFragment {

    public VotePhaseFragment(Client client) {
        super(client);
    }

    @Override
    public void processGameState(GameState state) {
        if (!(state instanceof VotePhaseGameState)) {
            return; // also filters null
        }
        VotePhaseGameState s = (VotePhaseGameState) state;

        if (s.end) {
            Log.d("qwe", "got end");

            AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
            alert.setTitle("Evening news");
            alert.setMessage(client.getGameData().players.get(s.killedPlayer).name + " was killed today");
            alert.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", (dialog, which) -> dialog.dismiss());
            alert.show();

            Log.d("qwe", "processGameState: show");

        } else {
            for (int i = 0; i < s.cantChoose.length; i++) {
                pick.setEnablePickingRow(i, s.cantChoose[i] && (i != client.thisPlayerId()));
            }
        }
    }

    @Override
    public void onPhaseEnd() {
        GameState latest = client.getLatestGameState().getValue();
        if (latest instanceof VotePhaseGameState) {
            Log.d("qwe", "onPhaseEnd: end is " + ((VotePhaseGameState) latest).end);
        } else {
            Log.d("qwe", "onPhaseEnd: not a VotePhaseGameState");
        }
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("qwe", "onCreateView: !");
        View view = inflater.inflate(R.layout.vote_phase, container, false);

        TableLayout table = view.findViewById(R.id.voteTable);
        pick = new TablePick(getActivity(), client.getGameData(), table, 1);

        pick.setEnablePickingRow(client.thisPlayerId(), false);

        for (int playerId = 0; playerId < client.playerCount(); playerId++) {
            if (client.getGameData().players.get(playerId).dead) {
                Log.d("qwe", "onCreateView: dead player " + playerId);
                pick.setEnablePickingRow(playerId, false);
            }
        }

        pick.setColumnListener(0, picked -> {
            currentPick = picked;
            VotePhasePlayerAction action = new VotePhasePlayerAction(picked, false, client.thisPlayerId());
            client.sendPlayerAction(action);
        });

        client.getLatestGameState().observe(this, this::processGameState);
        processGameState(client.getLatestGameState().getValue());

        view.findViewById(R.id.votenext).setOnClickListener(v -> client.sendPlayerAction(TestPhasePlayerAction.nextPhase()));
        view.findViewById(R.id.voteFinal).setOnClickListener(v -> {
            Log.d("qwe", "onCreateView: final " + currentPick);
            if (currentPick != -1) {
                client.sendPlayerAction(
                        new VotePhasePlayerAction(currentPick,true, client.thisPlayerId())
                );
            }
        });

        return view;
    }

    private int currentPick = -1;
    private TablePick pick;
}
