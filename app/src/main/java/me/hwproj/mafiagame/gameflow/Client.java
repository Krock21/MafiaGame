package me.hwproj.mafiagame.gameflow;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.ConcurrentLinkedQueue;

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
        if (pack.isMeta()) {
            metaQueue.add(pack.getMeta());
            metaData.postValue(pack.getMeta());
        } else {
            receiveGameState(pack.getGameState());
        }
    }

    // from interractor's thread
    private void receiveGameState(FullGameState state) {
        fullGameState.postValue(state);
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
    private MutableLiveData<FullGameState> fullGameState = new MutableLiveData<>();
    private MutableLiveData<Integer> currentPhaseNumber = new MutableLiveData<>();
    private MutableLiveData<MetaCrouch> metaData = new MutableLiveData<>();
    private final ConcurrentLinkedQueue<MetaCrouch> metaQueue = new ConcurrentLinkedQueue<>();

    public Client(ClientSender sender, Settings settings, int thisPlayer) {
        currentGameState = new ClientGameData();
        for (GamePhase p : settings.phases) {
            currentGameState.phases.add(p.getClientPhase());
        }
        for (PlayerSettings p : settings.playerSettings) {
            currentGameState.players.add(p.constructPlayer());
        }

        currentPhaseNumber.setValue(0);

        metaData.observeForever(meta -> {
            while (!metaQueue.isEmpty()) {
                receiveMeta(metaQueue.poll());
            }
        });
        fullGameState.observeForever(state -> {
            getGameData().update(state);
            latestGameState.setValue(state.getPhaseState());
        });


        this.sender = sender;
        this.thisPlayer = thisPlayer;
    }

    public LiveData<GameState> getLatestGameState() {
        return latestGameState;
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
