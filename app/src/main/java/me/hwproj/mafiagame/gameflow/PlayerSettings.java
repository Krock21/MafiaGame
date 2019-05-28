package me.hwproj.mafiagame.gameflow;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import me.hwproj.mafiagame.gameplay.Role;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;

public class PlayerSettings {
    public Role role;
    public String name; // TODO make private

    public PlayerSettings(Role role, String name) {
        this.role = role;
        this.name = name;
    }

    Player constructPlayer() {
        return new Player(role, name);
    }

    public static PlayerSettings deserialize(DataInputStream stream) throws DeserializationException {
        int b;
        try {
            b = stream.read();
        } catch (IOException e) {
            throw new DeserializationException(e);
        }

        Role r = Role.deserialize((byte) b);
        String name;
        try {
            int len = stream.readInt();
            byte[] bytes = new byte[len];
            stream.read(bytes);
            name = new String(bytes); // TODO specify charset
        } catch (IOException e) {
            throw new DeserializationException("Cant read name", e);
        }

        return new PlayerSettings(r, name);
    }

    public void serialize(OutputStream stream) throws SerializationException {
        try {
            stream.write(Role.serialize(role));
        } catch (IOException e) {
            throw new SerializationException("Cant serialize role", e);
        }

        byte[] nameBytes = name.getBytes(); // TODO charset
        try (DataOutputStream dataStream = new DataOutputStream(stream)) {
            dataStream.writeInt(nameBytes.length);
            dataStream.write(nameBytes);
        } catch (IOException e) {
            throw new SerializationException("Cant serialize name", e);
        }
    }
}
