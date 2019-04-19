package me.hwproj.mafiagame.phases;

import me.hwproj.mafiagame.gameflow.GameData;

/**
 * Information which is sent from server to clients.
 */
public abstract class GameState {
    /**
     * A copy of GameData
     */
    GameData data;
}
