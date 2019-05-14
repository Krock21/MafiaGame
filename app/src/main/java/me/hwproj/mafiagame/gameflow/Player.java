package me.hwproj.mafiagame.gameflow;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.TreeSet;

import me.hwproj.mafiagame.gameplay.Effect;
import me.hwproj.mafiagame.gameplay.Role;

public class Player {
    final public Role role;
    final public String name;
    public  boolean hasDead;
    public Set<Effect> effects = new TreeSet<>();

    public Player(Role role, String name) {
        this.role = role;
        this.name = name;
    }

    public void kill() {
    }
}
