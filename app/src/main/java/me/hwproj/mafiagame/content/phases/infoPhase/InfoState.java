package me.hwproj.mafiagame.content.phases.infoPhase;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phases.GameState;

public class InfoState extends GameState {
//    private final boolean
    private final List<String> phasesInformation;

    public InfoState(List<String> phasesInformation) {
        this.phasesInformation = phasesInformation;
    }

    public List<String> getPhasesInformation() {
        return phasesInformation;
    }

    public void serialize(DataOutputStream dout) throws SerializationException {
        try {
            dout.writeInt(phasesInformation.size());
            for (String s : getPhasesInformation()) {
                dout.writeUTF(s);
            }
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }


    public static InfoState deserialize(DataInputStream din) throws DeserializationException {
        try {
            int stringCount = din.readInt();
            ArrayList<String> strings = new ArrayList<>();
            strings.ensureCapacity(stringCount);

            for (int i = 0; i < stringCount; i++) {
                strings.add(din.readUTF());
            }

            return new InfoState(strings);

        } catch (IOException e) {
            throw new DeserializationException(e);
        }
    }
}
