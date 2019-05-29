package me.hwproj.mafiagame.content.phases.investigator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.hwproj.mafiagame.content.phases.abstractpick.PickState;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phases.GameState;

public class InvState extends GameState {
    private boolean isInvestigationResult;
    private boolean isGood;
    private PickState pickState;

    public InvState(PickState pickState) {
        this.pickState = pickState;
        this.isInvestigationResult = false;
    }

    public InvState(PickState pickState, boolean isGood) {
        this.pickState = pickState;
        this.isInvestigationResult = true;
        this.isGood = isGood;
    }

    private InvState(PickState pick, boolean isInvestigationResult, boolean isGood) {
        pickState = pick;
        this.isInvestigationResult = isInvestigationResult;
        this.isGood = isGood;
    }

    public boolean isInvestigationResult() {
        return isInvestigationResult;
    }
    public boolean getIsGood() {
        return isGood;
    }

    public PickState getPickState() {
        return pickState;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    void serialize(DataOutputStream dout) throws SerializationException, IOException {
        dout.writeBoolean(isInvestigationResult);
        dout.writeBoolean(isGood);
        pickState.serialize(dout);
    }

    public static InvState deserialize(DataInputStream din) throws IOException, DeserializationException {
        boolean isResult = din.readBoolean();
        boolean isGood = din.readBoolean();
        PickState pick = PickState.deserialize(din);
        return new InvState(pick, isResult, isGood);
    }
}
