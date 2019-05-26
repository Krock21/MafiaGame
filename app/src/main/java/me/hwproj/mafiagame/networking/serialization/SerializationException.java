package me.hwproj.mafiagame.networking.serialization;

public class SerializationException extends Exception {
    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(Throwable cause) {
        super(cause);
    }
}
