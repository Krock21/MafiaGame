package me.hwproj.mafiagame.content.phases.vote;

import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.phases.GamePhaseClient;
import me.hwproj.mafiagame.phases.PhaseFragment;

class VotePhaseClient implements GamePhaseClient {
    @Override
    public PhaseFragment createFragment(Client client) {
        return new VotePhaseFragment(client);
    }
}
