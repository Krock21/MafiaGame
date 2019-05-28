package me.hwproj.mafiagame.content.phases.vote;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.hwproj.mafiagame.content.phases.abstractpick.PickAction;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phases.PlayerAction;

public class VotePhasePlayerAction extends PlayerAction {
    public final int chosenPlayerNumber;
    public final boolean fixed;
    public final int thisPlayer;
    public VotePhasePlayerAction(int chosenPlayerNumber, boolean fixed, int thisPlayer) {
        this.chosenPlayerNumber = chosenPlayerNumber;
        this.fixed = fixed;
        this.thisPlayer = thisPlayer;
    }

    public void serialize(DataOutputStream dout) throws SerializationException {
        try {
            dout.writeBoolean(fixed);
            dout.writeInt(chosenPlayerNumber);
            dout.writeInt(thisPlayer);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    public static VotePhasePlayerAction deserialize(DataInputStream din) throws DeserializationException {
        try {
            boolean fixed = din.readBoolean();
            int chosenPlayerNumber = din.readInt();
            int thisPlayer = din.readInt();
            return new VotePhasePlayerAction(chosenPlayerNumber, fixed, thisPlayer);
        } catch (IOException e) {
            throw new DeserializationException(e);
        }
    }
}
