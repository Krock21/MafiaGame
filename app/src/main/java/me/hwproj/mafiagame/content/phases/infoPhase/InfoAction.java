package me.hwproj.mafiagame.content.phases.infoPhase;

import me.hwproj.mafiagame.phase.PlayerAction;

public class InfoAction extends PlayerAction {
    public final boolean wantsNext;
    public final int playerNumber;

    public InfoAction(boolean wantsNext, int playerNumber) {
        this.wantsNext = wantsNext;
        this.playerNumber = playerNumber;
    }
}
