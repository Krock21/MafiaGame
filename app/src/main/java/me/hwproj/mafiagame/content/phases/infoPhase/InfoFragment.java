package me.hwproj.mafiagame.content.phases.infoPhase;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import me.hwproj.mafiagame.R;
import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.phase.GameState;
import me.hwproj.mafiagame.phase.PhaseFragment;

public class InfoFragment extends PhaseFragment {

    private InfoState info;
    private LinearLayout infoHolder;

    public InfoFragment(Client client) {
        super(client);
    }

    @Override
    public void processGameState(GameState state) {
        if (!(state instanceof InfoState)) {
            return;
        }

        info = (InfoState) state;
        Log.d("Info", "processGameState: got info " + info);
        setInfo();
    }
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.info_phase, container, false);

        infoHolder = v.findViewById(R.id.info_holder);
        setInfo();

        CheckBox checkboxNext = v.findViewById(R.id.info_final);
        checkboxNext.setOnCheckedChangeListener((b, state) -> sendAction(state));

        subscribeToGameState();
        return v;
    }

    private void setInfo() {
        if (infoHolder == null || info == null) {
            return;
        }
        infoHolder.removeAllViews();
        for (String s : info.getPhasesInformation()) {
            addStringOnScreen(s);
        }
        Log.d("Info", "setInfo: " + info.getPhasesInformation() + " lines");

        // if first info
        if (client.getGameData().getCurrentPhaseNumber() < client.getGameData().phases.size()) {
            addStringOnScreen("Your role is " + client.thisPlayer().role);
            Log.d("Info", "setInfo: role information");
        }
    }

    @Override
    public void onPhaseEnd() {

    }

    private void addStringOnScreen(String s) {
        TextView line = new TextView(getContext());
        line.setText(s);
        infoHolder.addView(line);
    }

    private void sendAction(boolean wantNext) {
        client.sendPlayerAction(new InfoAction(wantNext, client.thisPlayerId()));
    }
}
