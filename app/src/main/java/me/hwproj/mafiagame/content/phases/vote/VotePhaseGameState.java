package me.hwproj.mafiagame.content.phases.vote;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phase.GameState;

public class VotePhaseGameState extends GameState {
    public boolean end;
    public boolean[] cantChoose;
    public int killedPlayer;

    public void serialize(DataOutputStream out) throws SerializationException {
        try {
            if (end) {
                out.writeByte(1);
                out.writeInt(killedPlayer);
                return;
            }

            out.writeByte(0);
            out.writeInt(cantChoose.length);
            for (boolean p : cantChoose) {
                out.writeBoolean(p);
            }
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    public static VotePhaseGameState deserialize(DataInputStream in) throws DeserializationException {
        try {
            byte b = in.readByte();
            if (b != 0) {
                int killedPlayer = in.readInt();
                return killed(killedPlayer);
            }

            int length = in.readInt();
            boolean[] cantChoose = new boolean[length];
            for (int i = 0; i < length; i++) {
                cantChoose[i] = in.readBoolean();
            }

            return choises(cantChoose);
        } catch (IOException e) {
            throw new DeserializationException(e);
        }
    }

    private static VotePhaseGameState killed(int killedPlayer) {
        VotePhaseGameState s = new VotePhaseGameState();
        s.end = true;
        s.killedPlayer = killedPlayer;
        return s;
    }

    private static VotePhaseGameState choises(boolean[] cantChoose) {
        VotePhaseGameState s = new VotePhaseGameState();
        s.end = false;
        s.cantChoose = cantChoose;
        return s;
    }
}
