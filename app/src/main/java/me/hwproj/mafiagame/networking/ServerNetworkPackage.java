package me.hwproj.mafiagame.networking;

public class ServerNetworkPackage {
    private final boolean isMeta;
    private final Object either;

    public ServerNetworkPackage(MetaInformation meta) {
        either = meta;
        isMeta = true;
    }

    public ServerNetworkPackage(FullGameState fullState) {
        either = fullState;
        isMeta = false;
    }

    public MetaInformation getMeta() {
        if (!isMeta) {
            return null;
        }
        return (MetaInformation) either;
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
