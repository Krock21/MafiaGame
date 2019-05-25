package me.hwproj.mafiagame.content.phases.mafia;

import android.util.Log;

import me.hwproj.mafiagame.content.effects.Murdered;
import me.hwproj.mafiagame.content.phases.abstractpick.PickState;
import me.hwproj.mafiagame.content.phases.abstractpick.PickServer;
import me.hwproj.mafiagame.gameflow.Player;
import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.gameplay.Role;
import me.hwproj.mafiagame.phases.PlayerAction;

public class MafiaServer extends PickServer {
    protected MafiaServer(Server serv) {
        super(serv, Role.MAFIA);
    }

    @Override
    protected void onPickComplete(int pickedPlayer) {

        Player victim = serv.currentGameData.players.get(pickedPlayer);
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
}
