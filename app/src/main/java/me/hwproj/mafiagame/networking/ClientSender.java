package me.hwproj.mafiagame.networking;

import me.hwproj.mafiagame.phase.PlayerAction;

public interface ClientSender {

    /**
     * Sends action to server
     */
    void sendPlayerAction(PlayerAction action);
}
