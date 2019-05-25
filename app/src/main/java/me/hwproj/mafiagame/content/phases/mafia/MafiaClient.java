package me.hwproj.mafiagame.content.phases.mafia;

import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.phases.GamePhaseClient;
import me.hwproj.mafiagame.phases.PhaseFragment;

public class MafiaClient implements GamePhaseClient {
    @Override
    public PhaseFragment createFragment(Client client) {
        return new MafiaFragment(client);
    }
}
