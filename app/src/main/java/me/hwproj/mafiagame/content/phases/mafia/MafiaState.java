package me.hwproj.mafiagame.content.phases.mafia;

import me.hwproj.mafiagame.content.phases.abstractpick.PickState;
import me.hwproj.mafiagame.phases.GameState;

public class MafiaState extends GameState {
    public PickState picks;

    public MafiaState(PickState pickState) {
        this.picks = pickState;
    }
}
