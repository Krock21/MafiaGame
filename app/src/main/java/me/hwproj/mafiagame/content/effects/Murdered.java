package me.hwproj.mafiagame.content.effects;

import me.hwproj.mafiagame.gameflow.ClientGameData;
import me.hwproj.mafiagame.gameflow.Player;
import me.hwproj.mafiagame.gameflow.ServerGameData;
import me.hwproj.mafiagame.gameplay.Effect;

public class Murdered extends Effect {

    public static final String PRIORITY = "M";
    public static final String DELETE_TAG = "delete_murdered";

    protected Murdered() {
        super(PRIORITY);
    }

    @Override
    public boolean affect(Player p, ServerGameData data) {
        p.dead = true;
        return true;
    }

    @Override
    public boolean applyTag(String tag) {
        return DELETE_TAG.equals(tag);
    }
}
