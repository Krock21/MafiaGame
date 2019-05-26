package me.hwproj.mafiagame.impltest.network;

import me.hwproj.mafiagame.networking.FullGameState;
import me.hwproj.mafiagame.networking.MetaInformation;
import me.hwproj.mafiagame.networking.ServerSender;

public interface ServerByteSender {

    void sendToEveryone(byte[] message);
}
