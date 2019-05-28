package me.hwproj.mafiagame.networking;

public interface ServerSender {

    void sendGameState(FullGameState state);
    void sendMetaInformation(MetaInformation info);
}
