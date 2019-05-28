package me.hwproj.mafiagame.networking.messaging;

// просто интерфейс для сервера
public interface ServerByteSender {
    // послать всем
    void broadcastMessage(byte[] message);

    // послать выбранному клиенту
    void sendMessage(String participantId, byte[] message);
}
