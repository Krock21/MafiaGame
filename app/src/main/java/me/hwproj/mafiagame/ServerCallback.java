package me.hwproj.mafiagame;

// принимается в какой-нибудь функции перед созданием комнаты
public interface ServerCallback {
    // вызывается когда получаем сообщение от клиента
    void receiveClientMessage(String participantId, byte[] message);
}
