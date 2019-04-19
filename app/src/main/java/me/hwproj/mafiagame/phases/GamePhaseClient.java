package me.hwproj.mafiagame.phases;

import android.app.Activity;

import me.hwproj.mafiagame.networking.ClientServerInteractor;
import me.hwproj.mafiagame.gameflow.GameData;

public interface GamePhaseClient {
    /**
     * Creates activity corresponding to this phase.
     * Actually it should probably create smth else, like View or smth, because all activities for
     * phases have similar things in them
     */
    Activity createActivity(GameData data, ClientServerInteractor sender);

    /**
     * This method should be called by when new GameState arrives.
     */
    void processGameState(GameState state);
}
