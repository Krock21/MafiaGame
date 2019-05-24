package me.hwproj.mafiagame.gameflow;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import me.hwproj.mafiagame.gameplay.Effect;
import me.hwproj.mafiagame.gameplay.Role;

public class Player {
    final public Role role;
    final public String name;
    public boolean dead;
    public List<Effect> effects = new ArrayList<>();

    public Player(Role role, String name) {
        this.role = role;
        this.name = name;
    }
}
