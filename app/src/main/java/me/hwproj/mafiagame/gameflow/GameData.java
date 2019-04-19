package me.hwproj.mafiagame.gameflow;

import java.util.ArrayList;
import java.util.List;

import me.hwproj.mafiagame.gameflow.Player;
import me.hwproj.mafiagame.phases.GamePhase;

public class GameData {
    private List<Player> players = new ArrayList<>();
    private GamePhase currentPhase;

    public GameData() {
    }
}
