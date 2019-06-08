package me.hwproj.mafiagame.phase;

import me.hwproj.mafiagame.gameflow.Server;

/**
 * An abstract class for describing game phases.
 * Provides server-size and client-side descriptions of a phase.
 */
public interface GamePhase {
    /**
     * This method is called once by a Server to get a server-side description of a phase
     * @return description of server-side phase behavior.
     */
    GamePhaseServer getServerPhase(Server server);

    /**
     * This method is called once by a Client to get a client-side description of a phase
     * @return description of client-side phase behavior.
     */
    GamePhaseClient getClientPhase();
}
