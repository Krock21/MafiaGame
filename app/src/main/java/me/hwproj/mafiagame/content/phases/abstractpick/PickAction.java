package me.hwproj.mafiagame.content.phases.abstractpick;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;

public class PickAction {
    final int pick;
    final boolean isFixed;
    /**
     * number among active pickers
     */
    final int playerNumber;

    public PickAction(int pick, boolean isFixed, int playerNumber) {
        this.pick = pick;
        this.isFixed = isFixed;
        this.playerNumber = playerNumber;
    }

    public void serialize(DataOutputStream dout) throws SerializationException {
        try {
            dout.writeBoolean(isFixed);
            dout.writeInt(pick);
            dout.writeInt(playerNumber);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    public static PickAction deserialize(DataInputStream din) throws DeserializationException {
        try {
            boolean isFixed = din.readBoolean();
            int pick = din.readInt();
            int playerNumber = din.readInt();
            return new PickAction(pick, isFixed, playerNumber);
        } catch (IOException e) {
            throw new DeserializationException(e);
        }
    }
}
