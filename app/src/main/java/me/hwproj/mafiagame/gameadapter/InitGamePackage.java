package me.hwproj.mafiagame.gameadapter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.hwproj.mafiagame.gameflow.Settings;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;

public class InitGamePackage {
    private Settings gameSettings;
    private int playerNumber;

    public InitGamePackage(Settings gameSettings, int playerNumber) {
        this.gameSettings = gameSettings;
        this.playerNumber = playerNumber;
    }

    public Settings getGameSettings() {
        return gameSettings;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public static InitGamePackage deserialize(DataInputStream message) throws DeserializationException {
        try {
            int thisPlayer = message.readInt();
            Settings s = Settings.deserialize(message);

            if (thisPlayer < 0 || thisPlayer > s.playerSettings.size()) {
                throw new DeserializationException("Incorrect player number: " + thisPlayer);
            }

            return new InitGamePackage(s, thisPlayer);
        } catch (IOException e) {
            throw new DeserializationException("Cant read int", e);
        }
    }

    public void serialize(DataOutputStream stream) throws SerializationException {
        try {
            stream.writeInt(playerNumber);
            gameSettings.serialize(stream);
        } catch (IOException e) {
            throw new SerializationException("Cant write int", e);
        }
    }
}
