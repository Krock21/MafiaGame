package me.hwproj.mafiagame.phases;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.phases.GameState;

public abstract class PhaseFragment extends Fragment {

    protected Client client; // to send info. TODO replace with smth more protected. Mb Sender?

    public PhaseFragment(Client client) {
        this.client = client;
    }

    public abstract void processGameState(GameState state);

    public abstract void onPhaseEnd();

}
