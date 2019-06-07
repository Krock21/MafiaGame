package me.hwproj.mafiagame.content.phases.mafia;

import me.hwproj.mafiagame.content.phases.abstractpick.PickAction;
import me.hwproj.mafiagame.phase.PlayerAction;

public class MafiaAction extends PlayerAction {
    private PickAction pickAction;
    public MafiaAction(PickAction pickAction) {
        this.pickAction = pickAction;
    }

    public PickAction getPickAction() {
        return pickAction;
    }

}
