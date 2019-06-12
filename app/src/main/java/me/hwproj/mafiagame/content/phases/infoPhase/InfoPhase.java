package me.hwproj.mafiagame.content.phases.infoPhase;

import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.phase.GamePhase;
import me.hwproj.mafiagame.phase.GamePhaseClient;
import me.hwproj.mafiagame.phase.GamePhaseServer;

/**
 * This is an utility phase for showing information to players.
 * It is the first phase every day and it show
 * {@link me.hwproj.mafiagame.gameflow.ServerGameData#infoToDisplay Server's infoToDisplay}
 *
 * It the first day it shows player's role instead.
 */
public class InfoPhase implements GamePhase {
    @Override
    public GamePhaseServer getServerPhase(Server server) {
        return new InfoServer(server);
    }

    @Override
    public GamePhaseClient getClientPhase() {
        return new InfoClient();
    }
}
