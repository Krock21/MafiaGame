package me.hwproj.mafiagame.phases;

import androidx.appcompat.app.AppCompatActivity;

public interface GamePhaseClient {
    /**
     * Creates activity corresponding to this phase.
     * Actually it should probably create smth else, like View or smth, because all activities for
     * phases have similar things in them
     */
    Class<? extends AppCompatActivity> createActivity(/*ClientGameData data, ClientSender sender*/);

    /**
     * This method should be called by when new GameState arrives.
     */
    void processGameState(GameState state);
}
