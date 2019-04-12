package me.hwproj.mafiagame;

import org.jetbrains.annotations.NotNull;

public class Server implements Runnable {
    private @NotNull
    GameData currentGameData;
    public @NotNull
    Settings settings;

    public Server(@NotNull Settings settings, @NotNull GameData startGameData) {
        this.settings = settings;
        this.currentGameData = startGameData;
    }

    @Override
    public void run() {

    }
}
