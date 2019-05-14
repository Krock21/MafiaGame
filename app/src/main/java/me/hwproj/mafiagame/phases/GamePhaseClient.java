package me.hwproj.mafiagame.phases;

import me.hwproj.mafiagame.gameflow.Client;

public interface GamePhaseClient {
    /**
     * Returns a fragment corresponding to this phase.
     */
    PhaseFragment createFragment(Client client);

}
