package me.hwproj.mafiagame.content.effects;

import me.hwproj.mafiagame.gameflow.Player;
import me.hwproj.mafiagame.gameflow.ServerGameData;
import me.hwproj.mafiagame.gameplay.Effect;

/**
 * Heales player marked with Murdered effect by applying {@link Murdered#DELETE_TAG Murdered.DELETE_TAG}
 * @see Murdered
 */
public class Healed extends Effect {

    public static final String PRIORITY = "H";

    public Healed() {
        super(PRIORITY);
    }

    @Override
    public boolean affect(Player p, ServerGameData data) {
        p.applyTag(Murdered.DELETE_TAG);
        return true;
    }

    @Override
    public boolean applyTag(String tag) {
        return false;
    }
}
