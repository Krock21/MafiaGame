package me.hwproj.mafiagame.gameplay;

import me.hwproj.mafiagame.gameflow.ClientGameData;
import me.hwproj.mafiagame.gameflow.Player;
import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.gameflow.ServerGameData;

public abstract class Effect implements Comparable<Effect> {

    protected Effect(String priority) {
        this.priority = priority;
    }

    /**
     * Take effect on player.
     * @return if effect should be deleted afterwards
     */
    public abstract boolean affect(Player p, ServerGameData data);

    /**
     * API for interacting with effects.
     * Other effects (or smth else) can interact with this effect by applying tags to it.
     * Effect can ask to delete itself after applying tag.
     * @param tag tag to apply
     * @return <code>true</code> if effect should be deleted after applying tag
     */
    public abstract boolean applyTag(String tag);

    /**
     * All effects are resolved in order of their priority
     */
    public final String priority;

    public int compareTo(Effect other) {
        return priority.compareTo(other.priority);
    }
}
