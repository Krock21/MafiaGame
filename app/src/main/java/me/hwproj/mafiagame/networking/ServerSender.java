package me.hwproj.mafiagame.networking;

import me.hwproj.mafiagame.phases.GameState;

public interface ServerSender {

    void sendGameState(GameState state);
    void sendMetaInformation(MetaCrouch info);
}
