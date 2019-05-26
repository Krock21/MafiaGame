package me.hwproj.mafiagame.networking.messaging;

// теперь к клиенту
public interface ClientSender {
    // посылает сообщение серверу
    void sendToServer(byte[] message);
}
