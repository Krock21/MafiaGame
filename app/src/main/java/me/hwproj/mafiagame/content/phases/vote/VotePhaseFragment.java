package me.hwproj.mafiagame.content.phases.vote;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import me.hwproj.mafiagame.R;
import me.hwproj.mafiagame.TestStringHolder;
import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.gameflow.Player;
import me.hwproj.mafiagame.impltest.TestPhasePlayerAction;
import me.hwproj.mafiagame.phases.GameState;
import me.hwproj.mafiagame.phases.PhaseFragment;

class VotePhaseFragment extends PhaseFragment {
    public VotePhaseFragment(Client client) {
        super(client);
    }

    @Override
    public void processGameState(GameState state) {
        if (!(state instanceof VotePhaseGameState)) {
            return;
        }
        VotePhaseGameState s = (VotePhaseGameState) state;

        if (s.end) {
            client.getGameData().players.get(s.killedPlayer).kill();
        } else {
            cantChoose = s.cantChoose;
            for (int i = 0; i < cantChoose.length; i++) {
                if (cantChoose[i]) {
                    choiceButtons.get(i).setChecked(false);
                    choiceButtons.get(i).setActivated(false);
                }
            }
        }
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vote_phase, container, false);

        cantChoose = new boolean[client.playerCount()];
        choiceButtons = new ArrayList<>();

        TableLayout table = view.findViewById(R.id.voteTable);

        for (Player player : client.getGameData().players) {
            TableRow tableRow = new TableRow(getActivity());
            tableRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            TextView playerName = new TextView(getActivity());
            playerName.setText(player.name);
            tableRow.addView(playerName);

            RadioButton thisClientChoice = new RadioButton(getActivity());
            choiceButtons.add(thisClientChoice);
            tableRow.addView(thisClientChoice);

            for (Player otherPlayer : client.getGameData().players) {
                CheckBox otherChoice = new CheckBox(getActivity());
                otherChoice.setEnabled(false);
                // set listener
                tableRow.addView(otherChoice);
            }

            table.addView(tableRow);
        }

        client.getLatestGameState().observe(this, this::processGameState);
        processGameState(client.getLatestGameState().getValue());

        view.findViewById(R.id.votenext).setOnClickListener(v -> client.sendPlayerAction(TestPhasePlayerAction.nextPhase()));
        view.findViewById(R.id.voteFinal).setOnClickListener(v -> {
            if (currentPick != -1) {
                client.sendPlayerAction(
                        new VotePhasePlayerAction(currentPick,true, client.getThisPlayer())
                );
            }
        });
        setRadiobuttonListeners();

        return view;
    }

    private void setRadiobuttonListeners() {
        for (int i = 0; i < choiceButtons.size(); i++) {
            RadioButton b = choiceButtons.get(i);
            final int iCopy = i;
            b.setOnCheckedChangeListener((button, state) -> {
                if (!state) {
                    return;
                }

                for (int j = 0; j < choiceButtons.size(); j++) {
                    if (j != iCopy) {
                        choiceButtons.get(j).setChecked(false);
                    }
                }

                currentPick = iCopy;
                VotePhasePlayerAction action = new VotePhasePlayerAction(iCopy, false, client.getThisPlayer());
                client.sendPlayerAction(action);
            });
        }
    }

    private boolean[] cantChoose;
    private int currentPick = -1;
    private List<RadioButton> choiceButtons;
}
