package me.hwproj.mafiagame.content.phases.mafia;

import android.app.AlertDialog;

import me.hwproj.mafiagame.content.effects.Murdered;
import me.hwproj.mafiagame.content.phases.abstractpick.PickAction;
import me.hwproj.mafiagame.content.phases.abstractpick.PickFragment;
import me.hwproj.mafiagame.content.phases.vote.VotePhaseGameState;
import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.gameflow.Player;
import me.hwproj.mafiagame.gameplay.Role;
import me.hwproj.mafiagame.phases.GameState;

public class MafiaFragment extends PickFragment {
    public MafiaFragment(Client client) {
        super(client, Role.MAFIA, true);
    }

    @Override
    protected void onPickComplete(int pickedPlayer) {
        Player victim = client.getGameData().players.get(pickedPlayer);
        victim.addEffect(new Murdered());

        AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
        alert.setTitle("Murder log");
        alert.setMessage(victim.name + " was murdered by you");
        alert.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", (dialog, which) -> dialog.dismiss());
        alert.show();
    }

    @Override
    protected void sendPickAction(PickAction pickAction) {
        client.sendPlayerAction(new MafiaAction(pickAction));
    }

    @Override
    public void processGameState(GameState state) {
        if (!(state instanceof MafiaState)) {
            return; // also filters null
        }
        MafiaState s = (MafiaState) state;

        processPickedState(s.picks);
    }

    @Override
    public void onPhaseEnd() {

    }
}
