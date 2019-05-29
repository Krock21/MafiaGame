package me.hwproj.mafiagame;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import me.hwproj.mafiagame.content.phases.doctor.DoctorPhase;
import me.hwproj.mafiagame.content.phases.mafia.MafiaPhase;
import me.hwproj.mafiagame.content.phases.vote.VotePhase;
import me.hwproj.mafiagame.gameflow.PlayerSettings;
import me.hwproj.mafiagame.gameflow.Settings;
import me.hwproj.mafiagame.gameplay.Role;
import me.hwproj.mafiagame.impltest.TestPhase;
import me.hwproj.mafiagame.phases.GamePhase;
import me.hwproj.mafiagame.util.Alerter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConfigurationCompleteListener} interface
 * to handle interaction events.
 * Use the {@link GameConfigureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameConfigureFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PLAYER_COUNT = "param1";

    private int playerCount;

    private ConfigurationCompleteListener mListener;

    private int mafiaCount;
    private int doctorCount;

    public GameConfigureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param playerCount Number of players a game would contain.
     * @return A new instance of fragment GameConfigureFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GameConfigureFragment newInstance(int playerCount) {
        GameConfigureFragment fragment = new GameConfigureFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PLAYER_COUNT, playerCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playerCount = getArguments().getInt(ARG_PLAYER_COUNT);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_game_configure, container, false);

        SeekBar selectMafia = v.findViewById(R.id.mafiaSeekBar);
//        selectMafia.setMin(1); // requires API level 26. Anyway why not play a game without mafias?
        selectMafia.setMax(playerCount - 1);
        selectMafia.setOnSeekBarChangeListener(mafiaSeekBarListener);

        CheckBox doctorCheckBox = v.findViewById(R.id.doctorCheckBox);
        doctorCheckBox.setOnCheckedChangeListener((ignored, isChecked) -> {
            if (isChecked) {
                doctorCount = 1;
            } else {
                doctorCount = 0;
            }
        });

        Button tryToStartGame = v.findViewById(R.id.completeSettings);
        tryToStartGame.setOnClickListener(ignored -> startGame());

        return v;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void startGame() {
        if (mListener == null) {
            Log.d("Bug", "game configuration fragment: callback is not initialized");
            return;
        }

        int configuredPlayers = mafiaCount + doctorCount;
        if (configuredPlayers > playerCount) {
            Alerter.alert(getContext(), "Error", "Too many roles selected");
            return;
        }

        List<PlayerSettings> roles = new ArrayList<>();
        for (int i = 0; i < mafiaCount; i++) {
            roles.add(new PlayerSettings(Role.MAFIA, "configure later"));
        }
        for (int i = 0; i < doctorCount; i++) {
            roles.add(new PlayerSettings(Role.DOCTOR, "configure later"));
        }
        for (int i = 0; i < playerCount - configuredPlayers; i++) {
            roles.add(new PlayerSettings(Role.CITIZEN, "configure later"));
        }

        List<GamePhase> phases = new ArrayList<>();
        phases.add(new VotePhase());
        phases.add(new TestPhase());
        phases.add(new MafiaPhase());
        if (doctorCount > 0) {
            phases.add(new DoctorPhase());
        }

        Settings settings = new Settings(phases, roles);

        mListener.onConfigurationFinished(settings);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof ConfigurationCompleteListener) {
            mListener = (ConfigurationCompleteListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ConfigurationCompleteListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private final SeekBar.OnSeekBarChangeListener mafiaSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mafiaCount = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // do nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // do nothing
        }
    };

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface ConfigurationCompleteListener {
        void onConfigurationFinished(Settings settings);
    }
}