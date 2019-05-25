package me.hwproj.mafiagame.content.phases.doctor;

import me.hwproj.mafiagame.content.phases.abstractpick.PickState;
import me.hwproj.mafiagame.phases.GameState;

public class DoctorState extends GameState {
    private PickState picks;

    public DoctorState(PickState pickState) {
        this.picks = pickState;
    }

    public PickState getPicks() {
        return picks;
    }
}
