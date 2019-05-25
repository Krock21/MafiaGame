package me.hwproj.mafiagame.content.phases.doctor;

import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.phases.GamePhaseClient;
import me.hwproj.mafiagame.phases.PhaseFragment;
import me.hwproj.mafiagame.util.ModifiableBoolean;

class DoctorPhaseClient implements GamePhaseClient {
    @Override
    public PhaseFragment createFragment(Client client) {
        return new DoctorFragment(client, pickedSelfLastTime);
    }

    private ModifiableBoolean pickedSelfLastTime = new ModifiableBoolean(false);
}
