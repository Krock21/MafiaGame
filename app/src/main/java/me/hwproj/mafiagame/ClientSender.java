package me.hwproj.mafiagame;

// теперь к клиенту
public interface ClientSender {
    // посылает сообщение серверу
    void sendToServer(byte[] message);
}
