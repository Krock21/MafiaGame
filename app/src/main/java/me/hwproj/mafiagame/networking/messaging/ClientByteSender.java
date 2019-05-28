package me.hwproj.mafiagame.networking.messaging;

// теперь к клиенту
public interface ClientByteSender {
    // посылает сообщение серверу
    void sendBytesToServer(byte[] message);
}
