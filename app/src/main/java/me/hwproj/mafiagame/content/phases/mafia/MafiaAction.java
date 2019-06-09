package me.hwproj.mafiagame.content.phases.mafia;

import me.hwproj.mafiagame.content.phases.abstractpick.PickAction;
import me.hwproj.mafiagame.phase.PlayerAction;

class MafiaAction extends PlayerAction {
    private final PickAction pickAction;
    public MafiaAction(PickAction pickAction) {
        this.pickAction = pickAction;
    }

    public PickAction getPickAction() {
        return pickAction;
    }

}
