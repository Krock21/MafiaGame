package me.hwproj.mafiagame.content.phases.impltest;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;

import org.jetbrains.annotations.NotNull;

import me.hwproj.mafiagame.R;
import me.hwproj.mafiagame.util.TestStringHolder;
import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.phase.GameState;
import me.hwproj.mafiagame.phase.PhaseFragment;

public class TestPhaseFragment extends PhaseFragment {

    public TestPhaseFragment(Client client) {
        super(client);
    }

    @Override
    public void processGameState(GameState state) {

        if (!(state instanceof TestPhaseGameState)) {
            return;
        }
        ViewModelProviders.of(this).get(TestStringHolder.class)
                .setText(String.valueOf(((TestPhaseGameState) state).getSum()));
    }

    @Override
    public void onPhaseEnd() {

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.test_phase, container, false);

        TextView text = view.findViewById(R.id.sumView);
        TestStringHolder h = ViewModelProviders.of(this).get(TestStringHolder.class);
        h.getData().observe(this, text::setText);

        Button b = view.findViewById(R.id.increaseSum);
        b.setOnClickListener(v -> client.sendPlayerAction(new TestPhasePlayerAction(2)));

        Button bnext = view.findViewById(R.id.nextButton);
        bnext.setOnClickListener(v -> client.sendPlayerAction(TestPhasePlayerAction.nextPhase()));

        subscribeToGameState();

        Log.d("Ok", "Created view");

        return view;
    }
}
