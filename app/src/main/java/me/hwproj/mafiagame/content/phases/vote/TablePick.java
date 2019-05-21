package me.hwproj.mafiagame.content.phases.vote;

import me.hwproj.mafiagame.gameflow.ClientGameData;

public class TablePick {
    private final ClientGameData data;
    private final int pickColumns;
    private final int additionalColumns;
    
    private final boolean[] frobiddenPicks;


    public TablePick(ClientGameData data, int pickColumns, int additionalColumns) {
        this.data = data;
        this.pickColumns = pickColumns;
        this.additionalColumns = additionalColumns;
        frobiddenPicks = new boolean[data.players.size()];
    }

    public TablePick(ClientGameData data) {
        this(data, 1, 0);
    }


    public void permitPicking(int playerId) {

    }

    public void forbidPicking(int playerId) {
        
    }
}