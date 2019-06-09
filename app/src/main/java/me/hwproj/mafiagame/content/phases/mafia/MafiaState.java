package me.hwproj.mafiagame.content.phases.mafia;

import me.hwproj.mafiagame.content.phases.abstractpick.PickState;
import me.hwproj.mafiagame.phase.GameState;

class MafiaState extends GameState {
    public final PickState picks;

    public MafiaState(PickState pickState) {
        this.picks = pickState;
    }
}
