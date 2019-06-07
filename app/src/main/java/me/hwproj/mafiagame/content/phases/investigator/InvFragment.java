package me.hwproj.mafiagame.content.phases.investigator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import me.hwproj.mafiagame.content.phases.abstractpick.PickAction;
import me.hwproj.mafiagame.content.phases.abstractpick.PickFragment;
import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.gameplay.Role;
import me.hwproj.mafiagame.phase.GameState;
import me.hwproj.mafiagame.util.Alerter;

class InvFragment extends PickFragment {
    public InvFragment(Client client) {
        super(client, Role.INVESTIGATOR, false);
    }

    @Override
    public void processGameState(GameState state) {
        if (!(state instanceof InvState)) {
            return;
        }

        if (isNotYourTurn())  {
            return;
        }

        InvState invState = (InvState) state;

        if (invState.isInvestigationResult()) {
            int playerNumber = invState.getPickState().getPicked();
            String message = "Player " + client.getGameData().players.get(playerNumber).name + " is ";
            if (invState.getIsGood()) {
                message += "good";
            } else {
                message += "bad";
            }
            Alerter.alert(getContext(), "Investigation result", message);
        }
        processPickedState(((InvState) state).getPickState());
    }

    @Override
    public void onPhaseEnd() {

    }

    @Override
    protected void onPickComplete(int pickedPlayer) {
        // Better to rely on server's information about allegiance
    }

    @Override
    protected void sendPickAction(PickAction pickPhasePlayerAction) {
        client.sendPlayerAction(new InvAction(pickPhasePlayerAction));
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        subscribeToGameState();
        return v;
    }
}
