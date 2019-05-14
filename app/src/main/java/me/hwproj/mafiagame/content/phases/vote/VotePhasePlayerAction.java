package me.hwproj.mafiagame.content.phases.vote;

import me.hwproj.mafiagame.phases.PlayerAction;

public class VotePhasePlayerAction extends PlayerAction {
    public final int chosenPlayerNumber;
    public final boolean fixed;
    public final int thisPlayer;
    public VotePhasePlayerAction(int chosenPlayerNumber, boolean fixed, int thisPlayer) {
        this.chosenPlayerNumber = chosenPlayerNumber;
        this.fixed = fixed;
        this.thisPlayer = thisPlayer;
    }

    @Override
    public String getPhaseName() {
        return null;
    }
}
