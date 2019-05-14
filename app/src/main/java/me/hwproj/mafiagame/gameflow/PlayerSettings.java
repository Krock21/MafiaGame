package me.hwproj.mafiagame.gameflow;

import me.hwproj.mafiagame.gameflow.Player;
import me.hwproj.mafiagame.gameplay.Role;

public class PlayerSettings {
    public final Role role;
    public final String name;

    public PlayerSettings(Role role, String name) {
        this.role = role;
        this.name = name;
    }

    Player constructPlayer() {
        return new Player(role, name);
    }
}
