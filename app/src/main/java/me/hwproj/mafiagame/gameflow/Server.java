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

/**
 * Represents all the server-side logic of the game once it is configured.
 * Handles client messages and provides interface for phases to affect the
 * game.
 */
public class Server {
    private ServerGameData currentGameData;
    private ServerSender sender;

    // ------------ interface for ServerGame ------------

    public Server(@NotNull Settings settings, ServerSender sender) {
        this.currentGameData = new ServerGameData();
        this.sender = sender;

        for (PlayerSettings p : settings.getPlayerSettings()) {
            getGameData().players.add(p.constructPlayer());
        }
        for (GamePhase p : settings.phases) {
            getGameData().phases.add(p.getServerPhase(this));
        }
    }

    /**
     * Calling this starts the server.
     */
    public void start() {
        getGameData().startNextPhase();
    }

    /**
     * Handles received PlayerAction. It is guaranteed
     * @param action action to handle
     */
    public void acceptPlayerAction(PlayerAction action) {
        Log.d("Ok", "acceptPlayerAction: server got an acton " + action.getClass());
        Log.d("Ok", "server phase is: " + getGameData().getCurrentPhase().getClass());
        getGameData().getCurrentPhase().processPlayerAction(action);
    }

    // ------------ interface for phases ------------

    /**
     * Sends a GameState too all of the clients
     * @param gameState state to send
     */
    public void sendGameState(GameState gameState) {
        Log.d("Ok", "sendGameState: server sends a GameState " + gameState.getClass());
        sender.sendGameState(new FullGameState(currentGameData, gameState));
    }

    /**
     * Starts next game phase
     */
    public void startNextPhase() {
        if (getGameData().getCurrentPhase() != null) {
            getGameData().getCurrentPhase().onEnd();
        }

        getGameData().endThisPhase();

        boolean anyGoodAlive = false;
        boolean anyBadAlive = false;
        for (Player p : getGameData().players) {
            if (!p.dead) {
                anyBadAlive |= !p.role.isGood();
                anyGoodAlive |= p.role.isGood();
            }
        }

        if (!anyBadAlive || !anyGoodAlive) {
            finishGame(anyGoodAlive);
            return;
        }

        getGameData().startNextPhase();
        sender.sendMetaInformation(MetaInformation.nextPhase(getGameData().getCurrentPhaseNumber()));
    }

    /**
     * What to do then the game is finished
     * @param goodWon if good guys won
     */
    private void finishGame(boolean goodWon) {
        sender.sendMetaInformation(MetaInformation.endGame(goodWon));
    }

    // --------------- for everything --------------

    public ServerGameData getGameData() {
        return currentGameData;
    }

    // -------- specifically for Info phase --------

    /**
     * Gets information saved for the info phase to show
     * @return information to show in the info phase
     */
    public List<String> getInfo() {
        return getGameData().infoToDisplay;
    }

    /**
     * Clears information for info phase
     */
    public void clearInfo() {
        getGameData().infoToDisplay.clear();
    }
}
