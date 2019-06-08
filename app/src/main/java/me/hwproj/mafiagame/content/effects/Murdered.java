package me.hwproj.mafiagame.content.effects;

import android.util.Log;

import me.hwproj.mafiagame.gameflow.ClientGameData;
import me.hwproj.mafiagame.gameflow.Player;
import me.hwproj.mafiagame.gameflow.ServerGameData;
import me.hwproj.mafiagame.gameplay.Effect;

/**
 * Player marked with this tag will die unless healed with {@link Murdered#DELETE_TAG DELETE_TAG}
 */
public class Murdered extends Effect {

    public static final String PRIORITY = "M";
    public static final String DELETE_TAG = "delete_murdered";

    public Murdered() {
        super(PRIORITY);
    }

    @Override
    public boolean affect(Player p, ServerGameData data) {
        p.dead = true;
        Log.d("Effect", "murdered: " + p.name);
        data.infoToDisplay.add(p.name + " was murdered by mafia");
        return true;
    }

    @Override
    public boolean applyTag(String tag) {
        return DELETE_TAG.equals(tag);
    }
}
