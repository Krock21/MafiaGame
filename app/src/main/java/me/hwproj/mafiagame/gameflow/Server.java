package me.hwproj.mafiagame.gameflow;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import me.hwproj.mafiagame.networking.FullGameState;
import me.hwproj.mafiagame.networking.MetaInformation;
import me.hwproj.mafiagame.networking.ServerSender;
import me.hwproj.mafiagame.phase.GamePhase;
import me.hwproj.mafiagame.phase.GameState;
import me.hwproj.mafiagame.phase.PlayerAction;

public class Server {
    // TODO private
    public ServerGameData currentGameData;
    public Settings settings;

    private ServerSender sender; // TODO make not public

    // ------------ interface for ServerGame ------------

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
    }

    /**
     * Calling this starts a server.
     */
    public void initialize() {
        currentGameData.startNextPhase();
    }

    public void acceptPlayerAction(PlayerAction action) {
        Log.d("Ok", "acceptPlayerAction: server got an acton " + action.getClass());
        Log.d("Ok", "server phase is: " + currentGameData.currentPhase.getClass());
        currentGameData.currentPhase.processPlayerAction(action);
    }

    // ------------ interface for phases ------------

    public void sendGameState(GameState gameState) {
        Log.d("Ok", "sendGameState: server sends a GameState " + gameState.getClass());
        sender.sendGameState(new FullGameState(currentGameData, gameState));
    }
    public void startNextPhase() {
        if (currentGameData.currentPhase != null) {
            currentGameData.currentPhase.onEnd();
        }

        currentGameData.endThisPhase();

        boolean anyGoodAlive = false;
        boolean anyBadAlive = false;
        for (Player p : currentGameData.players) {
            if (!p.dead) {
                anyBadAlive |= !p.role.isGood();
                anyGoodAlive |= p.role.isGood();
            }
        }

        if (!anyBadAlive || !anyGoodAlive) {
            finishGame(anyGoodAlive);
            return;
        }

        currentGameData.startNextPhase();
        sender.sendMetaInformation(MetaInformation.nextPhase(currentGameData.phaseNumber));
    }

    private void finishGame(boolean goodWon) {
        sender.sendMetaInformation(MetaInformation.endGame(goodWon));
    }

    // -------- specifically for Info phase --------

    public List<String> getInfo() {
        return currentGameData.infoToDisplay;
    }

    public void clearInfo() {
        currentGameData.infoToDisplay.clear();
    }

    // -------------- various getters --------------

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
