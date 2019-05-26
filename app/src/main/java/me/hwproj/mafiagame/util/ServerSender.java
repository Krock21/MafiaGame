// просто интерфейс для сервера
interface ServerSender {
    // послать всем
    void broadcastMesage(byte[] message);

    // послать выбранному клиенту
    void sendMessage(String participantId, byte[] message);
}