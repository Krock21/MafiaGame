package me.hwproj.mafiagame;

// просто интерфейс для сервера
public interface ServerSender {
    // послать всем
    void broadcastMesage(byte[] message);

    // послать выбранному клиенту
    void sendMessage(String participantId, byte[] message);
}
