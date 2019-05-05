package me.hwproj.mafiagame.gameplay;

import me.hwproj.mafiagame.gameflow.ClientGameData;
import me.hwproj.mafiagame.gameflow.Player;

public interface Effect {
    /**
     * Take effect on player.
     */
    void affect(Player p, ClientGameData data);
}
