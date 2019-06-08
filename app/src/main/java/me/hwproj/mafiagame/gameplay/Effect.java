package me.hwproj.mafiagame.gameplay;

import me.hwproj.mafiagame.gameflow.Player;
import me.hwproj.mafiagame.gameflow.ServerGameData;

/**
 * An effect can be attached to a player and it's {@link #affect(Player, ServerGameData) affect} method
 * will be called in the end of the night.
 *
 * Methods are applied to each player in order of their priority.
 */
public abstract class Effect implements Comparable<Effect> {

    /**
     * Constructs a new Effect
     * @param priority priority of this effect
     */
    protected Effect(String priority) {
        this.priority = priority;
    }

    /**
     * Take effect on player.
     * @return if effect should be deleted immediately afterwards
     */
    public abstract boolean affect(Player p, ServerGameData data);

    /**
     * API for interacting with effects.
     * Effect can choose to react to some tags in order to let other effects an interface for
     * interacting with it.
     * The most important is that effect can ask to delete itself after being applied with a tag.
     *
     * Player's {@link Player#applyTag(String)} can be used by other effects to interact with
     * all effects that choose to listen to this tag.
     *
     * For example, any new role can create effects that are removed by {@link me.hwproj.mafiagame.content.effects.Healed healing}
     * effect or simulate healing effect by applying the same tag.
     *
     * @see Player#applyTag(String)
     * @param tag tag to apply
     * @return <code>true</code> if effect should be deleted after applying provided tag
     */
    public abstract boolean applyTag(String tag);

    /**
     * All effects are resolved in order of their priority.
     *
     */
    private final String priority;

    public int compareTo(Effect other) {
        return priority.compareTo(other.priority);
    }
}
