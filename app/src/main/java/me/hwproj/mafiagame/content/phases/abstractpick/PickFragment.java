package me.hwproj.mafiagame.content.phases.abstractpick;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import me.hwproj.mafiagame.R;
import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.gameflow.Player;
import me.hwproj.mafiagame.gameplay.Role;
import me.hwproj.mafiagame.phases.PhaseFragment;
import me.hwproj.mafiagame.util.table.TablePick;

// TODO make a factory
abstract public class PickFragment extends PhaseFragment {
    private final Role pickersRole;
    private final boolean pickSelfRole;
    private final int[] thisRolePlayers;
    private int thisPlayerNumber = -1;
    private TablePick table;
    private int currentPick = -1;

    public PickFragment(Client client, Role pickersRole, boolean pickSelfRole) {
        super(client);
        this.pickersRole = pickersRole;
        this.pickSelfRole = pickSelfRole;

        List<Integer> thisRoleIds = new ArrayList<>();
        for (int i = 0; i < client.playerCount(); i++) {
            Player p = client.getGameData().players.get(i);
            if (p.role == pickersRole && !p.dead) {
                if (i == client.thisPlayerId()) {
                    thisPlayerNumber = thisRoleIds.size();
                }
                thisRoleIds.add(i);
            }
        }

        thisRolePlayers = new int[thisRoleIds.size()];
        for (int i = 0; i < thisRolePlayers.length; i++) {
            thisRolePlayers[i] = thisRoleIds.get(i);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.generic_pick, container, false);

        if (client.getGameData().players.get(client.thisPlayerId()).role != pickersRole) {
            TextView text = view.findViewById(R.id.pickNotYourTurn);
            text.setText("Not your turn");
            return view;
        }

        table = new TablePick(getContext(), client.getGameData(),
                view.findViewById(R.id.pickTable), thisRolePlayers.length);

        for (int i = 1; i < thisRolePlayers.length; i++) {
            table.setEnablePickingColumn(i, false);
        }

        if (!pickSelfRole) {
            for (int i : thisRolePlayers) {
                table.setEnablePickingRow(i, false);
            }
        }

        for (int i = 0; i < client.playerCount(); i++) {
            if (client.getGameData().players.get(i).dead) {
                table.setEnablePickingRow(i, false);
            }
        }

        table.setColumnListener(0, pick -> {
            currentPick = pick;
            sendPickAction(new PickAction(currentPick, false, thisPlayerNumber));
        });

        view.findViewById(R.id.pickFinal).setOnClickListener(v -> {
            if (currentPick != -1) {
                sendPickAction(new PickAction(currentPick, true, thisPlayerNumber));
            }
        });

        return view;
    }

    //    @Override
//    public void processGameState(GameState state) {
//
//    }

    protected void processPickedState(PickState data) {
        if (data.end) {
            Log.d("pick", "processPickedState: end");
            onPickComplete(data.pickedPlayer);
            return;
        }

        int metThisPlayer = 0;
        for (int i = 0; i < thisRolePlayers.length; i++) {
            int playerId = thisRolePlayers[i];
            if (playerId != client.thisPlayerId()) {
                table.setColumnPick(i + 1 - metThisPlayer, data.picks[i]);
            } else {
                metThisPlayer = 1;
            }
        }
    }

    protected abstract void onPickComplete(int pickedPlayer);

    protected abstract void sendPickAction(PickAction pickPhasePlayerAction);

//    @Override
//    public void onPhaseEnd() {
//
//    }
}
