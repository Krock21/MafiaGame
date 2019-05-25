package me.hwproj.mafiagame.content.phases.abstractpick;

public class PickAction {
    final int pick;
    final boolean isFixed;
    /**
     * number among active pickers
     */
    final int playerNumber;

    public PickAction(int pick, boolean isFixed, int playerNumber) {
        this.pick = pick;
        this.isFixed = isFixed;
        this.playerNumber = playerNumber;
    }
}
