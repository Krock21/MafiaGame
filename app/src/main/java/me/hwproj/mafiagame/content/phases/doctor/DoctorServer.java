package me.hwproj.mafiagame.content.phases.doctor;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import me.hwproj.mafiagame.content.effects.Healed;
import me.hwproj.mafiagame.content.phases.abstractpick.PickAction;
import me.hwproj.mafiagame.content.phases.abstractpick.PickServer;
import me.hwproj.mafiagame.content.phases.abstractpick.PickState;
import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.gameplay.Role;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phases.GameState;
import me.hwproj.mafiagame.phases.PlayerAction;

public class DoctorServer extends PickServer {
    protected DoctorServer(Server serv) {
        super(serv, Role.DOCTOR);
    }

    @Override
    protected void onPickComplete(int pickedPlayer) {
        serv.currentGameData.players.get(pickedPlayer).addEffect(new Healed());
        serv.startNextPhase();
    }

    @Override
    protected void sendPickState(PickState data) {
        serv.sendGameState(new DoctorState(data));
    }

    @Override
    public void processPlayerAction(PlayerAction action) {
        if (!(action instanceof DoctorAction)) {
            Log.d("bug", "processPlayerAction: doctor got " + action.getClass().toString());
            return;
        }
        processPickAction(((DoctorAction) action).getPickAction());
    }

    @Override
    public void onEnd() {

    }

    @Override
    public void serializeGameState(DataOutputStream dataOut, GameState state) throws SerializationException {
        if (!(state instanceof DoctorState)) {
            throw new SerializationException("wrong state");
        }
        DoctorState s = (DoctorState) state;
        s.getPicks().serialize(dataOut);
    }

    @Override
    public PlayerAction deserialize(DataInputStream dataStream) throws DeserializationException {
        return new DoctorAction(PickAction.deserialize(dataStream));
    }
}
