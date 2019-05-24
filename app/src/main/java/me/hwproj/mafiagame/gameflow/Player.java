package me.hwproj.mafiagame.gameflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.hwproj.mafiagame.gameplay.Effect;
import me.hwproj.mafiagame.gameplay.Role;

public class Player {
    final public Role role;
    final public String name;
    public boolean dead; // TODO make if a method or smth
    public List<Effect> effects = new ArrayList<>();

    public Player(Role role, String name) {
        this.role = role;
        this.name = name;
    }

    // interface for effects
    public void applyTag(String tag) {
        for (int i = currentEffectNumber + 1; i < effects.size();) {
            if (effects.get(i).applyTag(tag)) {
                effects.remove(i);
            } else {
                i++;
            }
        }
    }

    public void addEffect(Effect effect) {
        effects.add(effect);
    }

    private int currentEffectNumber;
    void resolveEffects(ServerGameData data) {
        Collections.sort(effects);
        for (currentEffectNumber = 0; currentEffectNumber < effects.size(); currentEffectNumber++) {
            effects.get(currentEffectNumber).affect(this, data);
        }
        currentEffectNumber = 0;
    }
}
