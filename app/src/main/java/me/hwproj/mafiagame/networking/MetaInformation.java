package me.hwproj.mafiagame.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;

public class MetaInformation {
    public static final int NEXT_PHASE = 1;

    private int whatInt;
    private int phaseNum;

    public static MetaInformation nextPhase(int num) {
        MetaInformation m = new MetaInformation();
        m.whatInt = NEXT_PHASE;
        m.phaseNum = num;
        return m;
    }

    public int what() {
        return whatInt;
    }

    public int getNumber() {
        return phaseNum;
    }

    public void serialize(DataOutputStream data) throws SerializationException {
        try {
            data.writeInt(whatInt);
            if (whatInt == NEXT_PHASE) {
                data.writeInt(phaseNum);
                return;
            }
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    public static MetaInformation deserialize(DataInputStream data) throws DeserializationException {
        try {
            int whatInt = data.readInt();
            if (whatInt != NEXT_PHASE) {
                throw new DeserializationException("Unrecognized whatInt");
            } else {
                return nextPhase(data.readInt());
            }
        } catch (IOException e) {
            throw new DeserializationException(e);
        }
    }

    private MetaInformation() {} // deleted
}
