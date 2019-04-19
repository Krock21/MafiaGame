package me.hwproj.mafiagame.gameplay;

import me.hwproj.mafiagame.gameflow.GameData;
import me.hwproj.mafiagame.gameflow.Player;

public interface Effect {
    /**
     * Take effect on player.
     */
    void affect(Player p, GameData data);
}
