package me.hwproj.mafiagame.content.phases.doctor;

import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.phase.GamePhase;
import me.hwproj.mafiagame.phase.GamePhaseClient;
import me.hwproj.mafiagame.phase.GamePhaseServer;

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
