package me.hwproj.mafiagame.content.phases.wait;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import me.hwproj.mafiagame.R;
import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.phase.GameState;
import me.hwproj.mafiagame.phase.PhaseFragment;

class WaitFragment extends PhaseFragment {
    public WaitFragment(Client client) {
        super(client);
    }

    @Override
    public void processGameState(GameState state) {
        if (!(state instanceof WaitState)) {
            return;
        }
        // actually now I understand it isn't needed
    }

    @Override
    public void onPhaseEnd() {

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wait_phase, container, false);

        // don't need to, but why not?
        subscribeToGameState();

        return view;
    }
}