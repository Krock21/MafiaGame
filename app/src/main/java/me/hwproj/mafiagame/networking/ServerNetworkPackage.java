package me.hwproj.mafiagame.networking;

import com.google.android.gms.games.Game;

import me.hwproj.mafiagame.phases.GameState;

public class ServerNetworkPackage {
    private final boolean isMeta;
    private final Object either;

    public ServerNetworkPackage(MetaCrouch meta) {
        either = meta;
        isMeta = true;
    }

    public ServerNetworkPackage(FullGameState fullState) {
        either = fullState;
        isMeta = false;
    }

    public MetaCrouch getMeta() {
        if (!isMeta) {
            return null;
        }
        return (MetaCrouch) either;
    }

    public FullGameState getGameState() {
        if (isMeta) {
            return null;
        }
        return (FullGameState) either;
    }

    public boolean isMeta() {
        return isMeta;
    }
}
