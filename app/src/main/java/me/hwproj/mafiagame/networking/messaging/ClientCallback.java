package me.hwproj.mafiagame.networking.messaging;

// принимается в функцию перед подключением.
// ну или сам реши где принимается
public interface ClientCallback {
    void receiveServerMessage(byte[] message);
}
