package me.hwproj.mafiagame.gameflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.hwproj.mafiagame.gameplay.Effect;
import me.hwproj.mafiagame.gameplay.Role;

/**
 * A description of a player.
 * Used by both server and client, but effects are purely server-side
 */
public class Player {
    final public Role role;
    final public String name;
    public boolean dead; // TODO make if a method or smth
    private final List<Effect> effects = new ArrayList<>();

    public Player(Role role, String name) {
        this.role = role;
        this.name = name;
    }

    /**
     * Applies provided tag to every effect AFTER the effect that applied the tag.
     * If called not by an effect, applies to every tag.
     * @param tag tag to apply
     */
    public void applyTag(String tag) {
        for (int i = currentEffectNumber + 1; i < effects.size();) {
            if (effects.get(i).applyTag(tag)) {
                effects.remove(i);
            } else {
                i++;
            }
        }
    }

    /**
     * Adds an effect to the player
     * @param effect effect to add
     */
    public void addEffect(Effect effect) {
        effects.add(effect);
    }

    /**
     * Used to apply effect's tag only to effects after it.
     */
    private int currentEffectNumber;

    /**
     * Applies all effects on this player in order of their priorities.
     * @param data game data to pass to effects
     */
    void resolveEffects(ServerGameData data) {
        Collections.sort(effects);
        for (currentEffectNumber = 0; currentEffectNumber < effects.size(); ) {
            if (effects.get(currentEffectNumber).affect(this, data)) {
                effects.remove(currentEffectNumber);
            } else {
                currentEffectNumber++;
            }
        }
        currentEffectNumber = 0;
    }
}
