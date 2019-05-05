package me.hwproj.mafiagame.gameflow;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import me.hwproj.mafiagame.impltest.NetworkSimulator;
import me.hwproj.mafiagame.impltest.TestPhaseGameState;
import me.hwproj.mafiagame.impltest.TestPhaseServer;
import me.hwproj.mafiagame.networking.MetaCrouch;
import me.hwproj.mafiagame.networking.ServerSender;
import me.hwproj.mafiagame.phases.GamePhase;
import me.hwproj.mafiagame.phases.PlayerAction;

public class Server {
    // TODO private
    public ServerGameData currentGameData;
    public Settings settings;

    public ServerSender sender; // TODO make not public


    public Server(@NotNull Settings settings, @NotNull ServerGameData startServerGameData, ServerSender sender) {
        this.settings = settings;
        this.currentGameData = startServerGameData;
        this.sender = sender; // TODO pass it or smth

        for (GamePhase p : settings.phases) {
            currentGameData.phases.add(p.getServerPhase());
        }
        currentGameData.nextPhase();
        for (int i = 0; i < settings.playerCount; i++) {
            currentGameData.players.add(new Player(null));
        }
    }

    public void acceptPlayerAction(PlayerAction action) {
        currentGameData.currentPhase.processPlayerAction(action, this);
    }

    // interface for phases
    public void sendGameState(TestPhaseGameState testPhaseGameState) {
        sender.sendGameState(testPhaseGameState);
    }
    // interface for phases
    public void startNextPhase() {
        currentGameData.currentPhase.onEnd();
        currentGameData.nextPhase();
        sender.sendMetaInformation(MetaCrouch.nextPhase(currentGameData.phaseNumber));
    }
}
