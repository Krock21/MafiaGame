package me.hwproj.mafiagame.content.phases.mafia;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import me.hwproj.mafiagame.content.effects.Murdered;
import me.hwproj.mafiagame.content.phases.abstractpick.PickAction;
import me.hwproj.mafiagame.content.phases.abstractpick.PickState;
import me.hwproj.mafiagame.content.phases.abstractpick.PickServer;
import me.hwproj.mafiagame.gameflow.Player;
import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.gameplay.Role;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phase.GameState;
import me.hwproj.mafiagame.phase.PlayerAction;

public class MafiaServer extends PickServer {
    MafiaServer(Server serv) {
        super(serv, Role.MAFIA);
    }

    @Override
    protected void onPickComplete(int pickedPlayer) {

        Player victim = serv.getGameData().players.get(pickedPlayer);
        victim.addEffect(new Murdered());

        serv.startNextPhase();
    }

    @Override
    protected void sendPickState(PickState data) {
        serv.sendGameState(new MafiaState(data));
        Log.d("qwe", "sendPickState: " + data.end);
    }

    @Override
    public void processPlayerAction(PlayerAction action) {
        if (!(action instanceof MafiaAction)) {
            Log.d("Bug", "action of class " + action.getClass().toString() + " in Vote server");
            return;
        }
        processPickAction(((MafiaAction) action).getPickAction());
    }

    @Override
    public void onEnd() {

    }

    @Override
    public void serializeGameState(DataOutputStream dataOut, GameState state) throws SerializationException {
        if (!(state instanceof MafiaState)) {
            throw new SerializationException("wrong state");
        }
        MafiaState s = (MafiaState) state;
        s.picks.serialize(dataOut);
    }

    @Override
    public PlayerAction deserialize(DataInputStream dataStream) throws DeserializationException {
        return new MafiaAction(PickAction.deserialize(dataStream));
    }
}
