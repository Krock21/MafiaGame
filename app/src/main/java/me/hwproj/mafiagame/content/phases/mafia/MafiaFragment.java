package me.hwproj.mafiagame.content.phases.mafia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import me.hwproj.mafiagame.content.phases.abstractpick.PickAction;
import me.hwproj.mafiagame.content.phases.abstractpick.PickFragment;
import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.gameflow.Player;
import me.hwproj.mafiagame.gameplay.Role;
import me.hwproj.mafiagame.phase.GameState;

import static me.hwproj.mafiagame.util.Alerter.alert;

public class MafiaFragment extends PickFragment {
    public MafiaFragment(Client client) {
        super(client, Role.MAFIA, true);
    }

    @Override
    protected void onPickComplete(int pickedPlayer) {
        if (isNotYourTurn()) {
            return;
        }
        Player victim = client.getGameData().players.get(pickedPlayer);
        alert(getContext(), "Murder log", victim.name + " was murdered by you");
    }

    @Override
    protected void sendPickAction(PickAction pickAction) {
        client.sendPlayerAction(new MafiaAction(pickAction));
    }

    @Override
    public void processGameState(GameState state) {
        if (!(state instanceof MafiaState)) {
            return; // also filters null
        }
        MafiaState s = (MafiaState) state;

        processPickedState(s.picks);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        subscribeToGameState();
        return v;
    }

    @Override
    public void onPhaseEnd() {

    }
}
