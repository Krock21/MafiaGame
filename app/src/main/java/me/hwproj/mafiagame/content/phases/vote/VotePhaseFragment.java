package me.hwproj.mafiagame.content.phases.vote;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import org.jetbrains.annotations.NotNull;

import me.hwproj.mafiagame.R;
import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.phase.GameState;
import me.hwproj.mafiagame.phase.PhaseFragment;
import me.hwproj.mafiagame.util.table.TablePick;

import static me.hwproj.mafiagame.util.Alerter.alert;

public class VotePhaseFragment extends PhaseFragment {

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
            String alersString;
            if (s.killedPlayer < client.playerCount()) {
                alersString = client.getGameData().players.get(s.killedPlayer).name + " was killed today";
            } else {
                alersString = "Nothing interesting happened this day";
            }
            alert(getContext(), "Evening news", alersString);

        } else {
            for (int i = 0; i < s.cantChoose.length; i++) {
                pick.setEnablePickingRow(i, !s.cantChoose[i]);
            }
        }
    }

    @Override
    public void onPhaseEnd() {
        GameState latest = client.getLatestGameState();
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
        View view = inflater.inflate(R.layout.generic_pick, container, false);

        TableLayout table = view.findViewById(R.id.pickTable);
        pick = new TablePick(getActivity(), client.getGameData(), table, 1, 0, true);

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

        subscribeToGameState();

        view.findViewById(R.id.pickFinal).setOnClickListener(v -> {
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
