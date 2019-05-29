package me.hwproj.mafiagame.networking.messaging;

// принимается в функцию перед подключением.
// ну или сам реши где принимается
// TODO rename so it won't be similar to ClientCallbacks
public interface ClientCallback {
    void receiveServerMessage(byte[] message);
}
