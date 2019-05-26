package me.hwproj.mafiagame.gameflow;

import android.util.Log;

import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.ConcurrentLinkedQueue;

import me.hwproj.mafiagame.content.phases.mafia.MafiaState;
import me.hwproj.mafiagame.networking.ClientSender;
import me.hwproj.mafiagame.networking.FullGameState;
import me.hwproj.mafiagame.networking.MetaCrouch;
import me.hwproj.mafiagame.networking.ServerNetworkPackage;
import me.hwproj.mafiagame.phases.GamePhase;
import me.hwproj.mafiagame.phases.GameState;
import me.hwproj.mafiagame.phases.PhaseFragment;
import me.hwproj.mafiagame.phases.PlayerAction;

// TODO separate phases' interface from interractor's and other code
public class Client {

    private final int thisPlayer;
    private final Consumer<GameState> gameStateHandler;

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
        getGameData().update(state);
        gameStateHandler.accept(state.getPhaseState());
    }

    private void handlePackage(ServerNetworkPackage pack) {
        Log.d("qwe", "handlePackage: handle meta " + pack.isMeta());
        if (pack.isMeta()) {
            Log.d("qwe", "handlePackage: META PACKAGE");
            receiveMeta(pack.getMeta());
        } else {
            if (pack.getGameState().getPhaseState() instanceof MafiaState) {
                MafiaState s = (MafiaState) pack.getGameState().getPhaseState();
                if (s.picks.end) {
                    Log.d("qwe", "MAFIA END");
                }
            }
            receiveState(pack.getGameState());
        }
    }

    public void startNextPhase(int number) {
        currentPhaseNumber.setValue(number);
    }

    // from UI thread
    private void receiveMeta(MetaCrouch metaCrouch) {
        Log.d("Ok", "receiveMeta: accepting meta");
        if (metaCrouch.what() == MetaCrouch.NEXT_PHASE) {
            startNextPhase(metaCrouch.getNumber());
        }
    }

    private ClientGameData currentGameState;
    private ClientSender sender; // TODO make smth out of it

    private MutableLiveData<GameState> latestGameState = new MutableLiveData<>();
    private MutableLiveData<Integer> currentPhaseNumber = new MutableLiveData<>();
    private MutableLiveData<ServerNetworkPackage> packageData = new MutableLiveData<>();
    private final ConcurrentLinkedQueue<ServerNetworkPackage> packageQueue = new ConcurrentLinkedQueue<>();

    public Client(ClientSender sender, Settings settings, int thisPlayer, Consumer<GameState> gameStateHandler) {
        this.gameStateHandler = gameStateHandler;
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
}
