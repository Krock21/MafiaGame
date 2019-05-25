package me.hwproj.mafiagame.content.phases.doctor;

import android.util.Log;

import me.hwproj.mafiagame.content.effects.Healed;
import me.hwproj.mafiagame.content.phases.abstractpick.PickServer;
import me.hwproj.mafiagame.content.phases.abstractpick.PickState;
import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.gameplay.Role;
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
}
