package me.hwproj.mafiagame.phase;

import androidx.fragment.app.Fragment;

import me.hwproj.mafiagame.gameflow.Client;

public abstract class PhaseFragment extends Fragment {

    protected Client client; // to send info. TODO replace with smth more protected. Mb Sender?

    public PhaseFragment(Client client) {
        this.client = client;
    }

    public abstract void processGameState(GameState state);

    public abstract void onPhaseEnd();

    protected final void subscribeToGameState() {
        subscribedToGameState = true;
        processGameState(client.getLatestGameState());
    }
    private boolean subscribedToGameState = false;

    public final boolean isSubscribedToGameState() {
        return subscribedToGameState;
    }
}
