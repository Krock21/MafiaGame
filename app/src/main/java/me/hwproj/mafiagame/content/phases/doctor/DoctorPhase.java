package me.hwproj.mafiagame.content.phases.doctor;

import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.phases.GamePhase;
import me.hwproj.mafiagame.phases.GamePhaseClient;
import me.hwproj.mafiagame.phases.GamePhaseServer;

public class DoctorPhase extends GamePhase {
    @Override
    public GamePhaseServer getServerPhase(Server server) {
        return new DoctorServer(server);
    }

    @Override
    public GamePhaseClient getClientPhase() {
        return new DoctorPhaseClient();
    }
}
