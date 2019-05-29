package me.hwproj.mafiagame.gameflow;

import android.util.Log;

import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.ConcurrentLinkedQueue;

import me.hwproj.mafiagame.content.phases.mafia.MafiaState;
import me.hwproj.mafiagame.networking.ClientSender;
import me.hwproj.mafiagame.networking.FullGameState;
import me.hwproj.mafiagame.networking.MetaInformation;
import me.hwproj.mafiagame.networking.ServerNetworkPackage;
import me.hwproj.mafiagame.phases.GamePhase;
import me.hwproj.mafiagame.phases.GameState;
import me.hwproj.mafiagame.phases.PhaseFragment;
import me.hwproj.mafiagame.phases.PlayerAction;
import me.hwproj.mafiagame.util.Alerter;

// TODO separate phases' interface from interractor's and other code
public class Client {

    private final int thisPlayer;
    private final ClientCallbacks callbacks;

    // from activity
    public void sendPlayerAction(PlayerAction action) {
        if (sender != null) { // TODO
            sender.sendPlayerAction(action);
        }
    }
    // from activity.
    public PhaseFragment nextPhaseFragment() {
        currentGameState.nextPhase();
        return currentGameState.currentPhase.createFragment(this);
    }

    // from interractor's thread
    public void receivePackage(ServerNetworkPackage pack) {
        Log.d("qwe", "receivePackage: meta: " + pack.isMeta());
        packageQueue.add(pack);
        packageData.postValue(pack); // mostly to call listeners
    }

    private void receiveState(FullGameState state) {
        Log.d("qwe", "receiveState: received");
        getGameData().update(state);
        callbacks.handleGameState(state.getPhaseState());
    }

    private void handlePackage(ServerNetworkPackage pack) {
        Log.d("qwe", "handlePackage: handle meta " + pack.isMeta());
        if (pack.isMeta()) {
            Log.d("qwe", "handlePackage: META PACKAGE");
            receiveMeta(pack.getMeta());
        } else {
            receiveState(pack.getGameState());
        }
    }

    public void startNextPhase(int number) {
        currentPhaseNumber.setValue(number);
    }

    // from UI thread
    private void receiveMeta(MetaInformation metaInformation) {
        Log.d("Ok", "receiveMeta: accepting meta");
        if (metaInformation.what() == MetaInformation.NEXT_PHASE) {
            startNextPhase(metaInformation.getNumber());
        } else if (metaInformation.what() == MetaInformation.END_GAME) {
            String finishMessage;
            if (metaInformation.getGoodWon()) {
                finishMessage = "Good won";
            } else {
                finishMessage = "Bad won";
            }
            finishGame(finishMessage);
        }
    }

    private void finishGame(String finishMessage) {
        callbacks.finishGame(finishMessage);
    }

    private ClientGameData currentGameState;
    private ClientSender sender; // TODO make smth out of it

    private MutableLiveData<GameState> latestGameState = new MutableLiveData<>();
    private MutableLiveData<Integer> currentPhaseNumber = new MutableLiveData<>();
    private MutableLiveData<ServerNetworkPackage> packageData = new MutableLiveData<>();
    private final ConcurrentLinkedQueue<ServerNetworkPackage> packageQueue = new ConcurrentLinkedQueue<>();

    public Client(ClientSender sender, Settings settings, int thisPlayer, ClientCallbacks callbacks) {
        this.callbacks = callbacks;
        currentGameState = new ClientGameData();
        for (GamePhase p : settings.phases) {
            currentGameState.phases.add(p.getClientPhase());
        }
        for (PlayerSettings p : settings.playerSettings) {
            currentGameState.players.add(p.constructPlayer());
        }

        currentPhaseNumber.setValue(0);

        packageData.observeForever(pack -> {
            while (!packageQueue.isEmpty()) {
                handlePackage(packageQueue.poll());
            }
        });


        this.sender = sender;
        this.thisPlayer = thisPlayer;
    }

    public GameState getLatestGameState() {
        return latestGameState.getValue();
    }

    public LiveData<Integer> getPhaseNumberData() {
        return currentPhaseNumber;
    }

    public ClientGameData getGameData() {
        return currentGameState;
    }

    public int playerCount() {
        return getGameData().players.size();
    }

    public int thisPlayerId() {
        return thisPlayer;
    }

    public Player thisPlayer() {
        return getGameData().players.get(thisPlayerId());
    }

    public void onThisPlayerKilled() {
        // currently does nothing
    }
}
