package me.hwproj.mafiagame.gameflow;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

import me.hwproj.mafiagame.gameplay.Role;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;

/**
 * A description of a player used while creating a game and
 * initializing Client/Server
 */
public class PlayerSettings {
    private Role role;
    public String name; // should be public

    public PlayerSettings(Role role, String name) {
        this.role = role;
        this.name = name;
    }

    public Role getRole() {
        return role;
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
            name = stream.readUTF();
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

        try (DataOutputStream dataStream = new DataOutputStream(stream)) {
            dataStream.writeUTF(name);
        } catch (IOException e) {
            throw new SerializationException("Cant serialize name", e);
        }
    }
}
