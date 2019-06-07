package me.hwproj.mafiagame.phase;

import me.hwproj.mafiagame.gameflow.Server;

/**
 * An abstract class for describing game phases.
 * Provides singletons of server-size and client-side descriptions of a phase.
 */
public abstract class GamePhase {
//    /**
//     * Name to check in sending and receiving network packages
//     * Should return a constant
//     */
//    public abstract String getPhaseName(); OK this does not work TODO

    /**
     * @return description of server-side phase behavior.
     */
    public abstract GamePhaseServer getServerPhase(Server server);

    /**
     * @return description of client-side phase behavior.
     */
    public abstract GamePhaseClient getClientPhase();
}
