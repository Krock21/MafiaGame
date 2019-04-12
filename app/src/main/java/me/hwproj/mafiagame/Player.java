package me.hwproj.mafiagame;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.TreeSet;

public class Player {
    final public @NotNull
    Role role;
    public  boolean hasDead;
    public Set<Effect> effects = new TreeSet<>();

    public Player(@NotNull Role role) {
        this.role = role;
    }
}
