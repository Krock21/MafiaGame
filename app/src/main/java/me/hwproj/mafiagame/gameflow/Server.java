package me.hwproj.mafiagame.gameflow;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import me.hwproj.mafiagame.networking.FullGameState;
import me.hwproj.mafiagame.networking.MetaInformation;
import me.hwproj.mafiagame.networking.ServerSender;
import me.hwproj.mafiagame.phases.GamePhase;
import me.hwproj.mafiagame.phases.GameState;
import me.hwproj.mafiagame.phases.PlayerAction;

public class Server {
    // TODO private
    public ServerGameData currentGameData;
    public Settings settings;

    public ServerSender sender; // TODO make not public


    public Server(@NotNull Settings settings, ServerSender sender) {
        this.settings = settings;
        this.currentGameData = new ServerGameData();
        this.sender = sender; // TODO pass it or smth

        for (PlayerSettings p : settings.playerSettings) {
            currentGameData.players.add(p.constructPlayer());
        }
        for (GamePhase p : settings.phases) {
            currentGameData.phases.add(p.getServerPhase(this));
        }
        currentGameData.nextPhase();
    }

    public void acceptPlayerAction(PlayerAction action) {
        Log.d("Ok", "acceptPlayerAction: server got an acton " + action.getClass());
        Log.d("Ok", "server phase is: " + currentGameData.currentPhase.getClass());
        currentGameData.currentPhase.processPlayerAction(action);
    }

    // interface for phases
    public void sendGameState(GameState gameState) {
        Log.d("Ok", "sendGameState: server sends a GameState " + gameState.getClass());
        sender.sendGameState(new FullGameState(currentGameData, gameState));
    }
    // interface for phases
    public void startNextPhase() {
        currentGameData.currentPhase.onEnd();
        currentGameData.nextPhase();
        sender.sendMetaInformation(MetaInformation.nextPhase(currentGameData.phaseNumber));
    }

    public int playerAliveCount() {
        int count = 0;
        for (Player p : currentGameData.players) {
            if (!p.dead) {
                count++;
            }
        }
        return count;
    }

    public int playerCount() {
        return currentGameData.players.size();
    }
}
