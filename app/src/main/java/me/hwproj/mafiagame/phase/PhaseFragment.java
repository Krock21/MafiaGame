package me.hwproj.mafiagame.phase;

import androidx.fragment.app.Fragment;

import me.hwproj.mafiagame.gameflow.Client;

/**
 * A fragment that is shown to players during the phase.
 * Each phase defines it's own PhaseFragment.
 */
public abstract class PhaseFragment extends Fragment {

    protected final Client client;

    protected PhaseFragment(Client client) {
        this.client = client;
    }

    /**
     * Receive a GameState that is not always sent by a server's part of this phase
     * @param state sent state
     */
    public abstract void processGameState(GameState state);

    /**
     * This method is called then the phase ends.
     * Soon the fragment will be replaced by some other
     */
    public abstract void onPhaseEnd();

    /**
     * Fragment should call this method to start receiving GameState.
     * Last GameState received by Client will be passed to the fragment immediately.
     */
    protected final void subscribeToGameState() {
        subscribedToGameState = true;
        processGameState(client.getLatestGameState());
    }

    private boolean subscribedToGameState = false;

    public final boolean isSubscribedToGameState() {
        return subscribedToGameState;
    }
}
