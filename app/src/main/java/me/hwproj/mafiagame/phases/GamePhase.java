package me.hwproj.mafiagame.phases;

import me.hwproj.mafiagame.phases.GamePhaseServer;

/**
 * An abstract class for describing game phases.
 * Provides singletons of server-size and client-side descriptions of a phase.
 */
public abstract class GamePhase {
    /**
     * Name to check in sending and receiving network packages
     * Actually final.
     * TODO mb methods should be used to simulate _static_ final fields in abstract classes?
     */
    private static String phaseName;

    /**
     * @return description of server-side phase behavior.
     */
    public abstract GamePhaseServer getServerPhase();

    /**
     * @return description of client-side phase behavior.
     */
    public abstract GamePhaseClient getClientPhase();
}
