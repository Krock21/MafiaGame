package me.hwproj.mafiagame.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;

/**
 * Metainformation sent by server to clients.
 * Everything that does not contain a GameState is considered metainformation
 */
public class MetaInformation {
    public static final int NEXT_PHASE = 1;
    public static final int END_GAME = 2;

    private int whatInt;
    private int phaseNum;
    private boolean goodWon;

    public static MetaInformation nextPhase(int num) {
        MetaInformation m = new MetaInformation();
        m.whatInt = NEXT_PHASE;
        m.phaseNum = num;
        return m;
    }

    public static MetaInformation endGame(boolean goodWon) {
        MetaInformation m = new MetaInformation();
        m.whatInt = END_GAME;
        m.goodWon = goodWon;
        return m;
    }

    public int what() {
        return whatInt;
    }

    public int getNumber() {
        return phaseNum;
    }

    public boolean getGoodWon() {
        return goodWon;
    }

    public void serialize(DataOutputStream data) throws SerializationException {
        try {
            data.writeInt(whatInt);
            if (whatInt == NEXT_PHASE) {
                data.writeInt(phaseNum);
                return;
            } else if (whatInt == END_GAME) {
                data.writeBoolean(goodWon);
            } else {
                throw new SerializationException("Unrecognized WHAT");
            }
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    public static MetaInformation deserialize(DataInputStream data) throws DeserializationException {
        try {
            int whatInt = data.readInt();
            if (whatInt == NEXT_PHASE) {
                return nextPhase(data.readInt());
            } else if (whatInt == END_GAME) {
                return endGame(data.readBoolean());
            } else {
                throw new DeserializationException("Unrecognized WHAT");
            }
        } catch (IOException e) {
            throw new DeserializationException(e);
        }
    }

    private MetaInformation() {} // deleted
}
