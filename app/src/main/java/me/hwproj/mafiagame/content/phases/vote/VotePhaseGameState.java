package me.hwproj.mafiagame.content.phases.vote;

import me.hwproj.mafiagame.phases.GameState;

public class VotePhaseGameState extends GameState {
    public boolean end;
    public boolean[] cantChoose;
    public int killedPlayer;
}
