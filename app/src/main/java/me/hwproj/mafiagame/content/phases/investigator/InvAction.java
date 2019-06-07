package me.hwproj.mafiagame.content.phases.investigator;

import me.hwproj.mafiagame.content.phases.abstractpick.PickAction;
import me.hwproj.mafiagame.phase.PlayerAction;

public class InvAction extends PlayerAction {
    private PickAction pick;

    public InvAction(PickAction pick) {
        this.pick = pick;
    }

    public PickAction getPick() {
        return pick;
    }
}
