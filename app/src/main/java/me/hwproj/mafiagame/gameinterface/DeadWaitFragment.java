package me.hwproj.mafiagame.gameinterface;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import me.hwproj.mafiagame.R;
import me.hwproj.mafiagame.menu.MainActivity;

/**
 * A fragment to show when player has died in the game.
 */
public class DeadWaitFragment extends Fragment {


    private final String content;
    private final boolean allowExit;

    /**
     * Constructs a new fragment instance
     * @param content   what to show to a player
     * @param allowExit if players is allowed to exit
     */
    public DeadWaitFragment(String content, boolean allowExit) {
        this.content = content;
        this.allowExit = allowExit;
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_server_wait, container, false);
        TextView text = v.findViewById(R.id.dead_info_holder);
        text.setText(content);

        Button exit = v.findViewById(R.id.exit);
        if (!allowExit) {
            exit.setVisibility(View.GONE);
        }
        exit.setOnClickListener( ignored ->
                Objects.requireNonNull(getContext()).startActivity(new Intent(getContext(), MainActivity.class))
        );

        return v;
    }

}
