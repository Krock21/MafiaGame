package me.hwproj.mafiagame;

// просто интерфейс для сервера
public interface ServerByteSender {
    // послать всем
    void broadcastMessage(byte[] message);

    // послать выбранному клиенту
    void sendMessage(String participantId, byte[] message);
}
