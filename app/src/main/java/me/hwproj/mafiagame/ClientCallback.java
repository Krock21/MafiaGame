package me.hwproj.mafiagame;

// принимается в функцию перед подключением.
// ну или сам реши где принимается
public interface ClientCallback {
    void receiveServerMessage(byte[] message);
}
