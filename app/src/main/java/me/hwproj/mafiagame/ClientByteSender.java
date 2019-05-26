package me.hwproj.mafiagame;

// теперь к клиенту
public interface ClientByteSender {
    // посылает сообщение серверу
    void sendBytesToServer(byte[] message);
}
