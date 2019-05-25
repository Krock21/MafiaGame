package me.hwproj.mafiagame.content.phases.doctor;

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
import me.hwproj.mafiagame.phases.GameState;
import me.hwproj.mafiagame.util.ModifiableBoolean;

import static me.hwproj.mafiagame.util.Alerter.alert;

public class DoctorFragment extends PickFragment {
    private final ModifiableBoolean pickedSelf;

    public DoctorFragment(Client client, ModifiableBoolean pickedSelf) {
        super(client, Role.DOCTOR, !pickedSelf.get());
        this.pickedSelf = pickedSelf;
    }

    @Override
    protected void onPickComplete(int pickedPlayer) {
        Player patient = client.getGameData().players.get(pickedPlayer);
        pickedSelf.set(pickedPlayer == client.thisPlayerId());
        alert(getContext(), "Healing log", "You healed " + patient.name);

    }

    @Override
    protected void sendPickAction(PickAction pickAction) {
        client.sendPlayerAction(new DoctorAction(pickAction));
    }

    @Override
    public void processGameState(GameState state) {
        if (!(state instanceof DoctorState)) {
            return;
        }
        DoctorState s = (DoctorState) state;

        processPickedState(s.getPicks());
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
