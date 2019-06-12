package me.hwproj.mafiagame.content.phases.investigator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.hwproj.mafiagame.content.phases.abstractpick.PickAction;
import me.hwproj.mafiagame.content.phases.abstractpick.PickServer;
import me.hwproj.mafiagame.content.phases.abstractpick.PickState;
import me.hwproj.mafiagame.gameflow.Player;
import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.gameplay.Role;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phase.GameState;
import me.hwproj.mafiagame.phase.PlayerAction;

class InvServer extends PickServer {
    public InvServer(Server server) {
        super(server, Role.INVESTIGATOR);
    }

    @Override
    protected void onPickComplete(int pickedPlayer) {
        // handling in sendPickState
        // only start next phase here
        serv.startNextPhase();
    }

    @Override
    protected void sendPickState(PickState data) {
        if (data.end) {
            Player pickedPlayer = serv.getGameData().players.get(data.getPicked());
            serv.sendGameState(new InvState(data, pickedPlayer.role.isGood()));
        } else {
            serv.sendGameState(new InvState(data));
        }
    }

    @Override
    public void processPlayerAction(PlayerAction action) {
        if (!(action instanceof InvAction)) {
            return;
        }
        processPickAction(((InvAction) action).getPick());
    }

    @Override
    public void onEnd() {

    }

    @Override
    public void serializeGameState(DataOutputStream dataOut, GameState state) throws SerializationException {
        if (!(state instanceof InvState)) {
            throw new SerializationException("wrong");
        }
        try {
            ((InvState) state).serialize(dataOut);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public PlayerAction deserialize(DataInputStream dataStream) throws DeserializationException {
        return new InvAction(PickAction.deserialize(dataStream));
    }
}
