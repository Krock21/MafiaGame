package me.hwproj.mafiagame.content.phases.abstractpick;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phase.GameState;

public class PickState extends GameState {
    int pickedPlayer; // TODO mb not package visible?
    int[] picks;
    public boolean end; // todo PACKAGE VISIBLE

    public static PickState pickedPickState(int pickedPlayer) {
        PickState s = new PickState();
        s.end = true;
        s.pickedPlayer = pickedPlayer;
        return s;
    }

    public static PickState picksPickState(int[] picks) {
        PickState s = new PickState();
        s.end = false;
        s.picks = picks;
        return s;
    }

    public void serialize(DataOutputStream out) throws SerializationException {
        try {
            if (end) {
                out.writeByte(1);
                out.writeInt(pickedPlayer);
                return;
            }

            out.writeByte(0);
            out.writeInt(picks.length);
            for (int p : picks) {
                out.writeInt(p);
            }
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    public static PickState deserialize(DataInputStream in) throws DeserializationException {
        try {
            byte b = in.readByte();
            if (b != 0) {
                int pickedPlayer = in.readInt();
                return pickedPickState(pickedPlayer);
            }

            int pickLength = in.readInt();
            int[] picks = new int[pickLength];
            for (int i = 0; i < pickLength; i++) {
                picks[i] = in.readInt();
            }

            return picksPickState(picks);
        } catch (IOException e) {
            throw new DeserializationException(e);
        }
    }

    public int getPicked() {
        return pickedPlayer;
    }

    public int[] getPicks() {
        return picks;
    }
}
