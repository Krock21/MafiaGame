package me.hwproj.mafiagame.gameflow;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import me.hwproj.mafiagame.impltest.NetworkSimulator;
import me.hwproj.mafiagame.impltest.TestPhase;
import me.hwproj.mafiagame.networking.ClientSender;
import me.hwproj.mafiagame.networking.MetaCrouch;
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
    // from activity. TODO remove argument
    public PhaseFragment nextPhaseFragment(Context context) {
        currentGameData.nextPhase();
        return currentGameData.currentPhase.createFragment(this);
    }

    // from interractor's thread
    public void receiveGameState(GameState state) {
        latestGameState.postValue(state);
    }
    // from interractor's thread
    public void startNextPhase(int number) {
        currentPhaseNumber.postValue(number);
    }
    // from interractor's thread
    public void receiveMeta(MetaCrouch metaCrouch) {
        if (metaCrouch.what() == MetaCrouch.NEXT_PHASE) {
            startNextPhase(metaCrouch.getNumber());
            return;
        }
    }

    private ClientGameData currentGameData;
    public ClientSender sender; // TODO make smth out of it

    private MutableLiveData<GameState> latestGameState;
    private MutableLiveData<Integer> currentPhaseNumber;

    public Client(ClientSender sender, Settings settings, int thisPlayer) {
        currentGameData = new ClientGameData();
        for (GamePhase p : settings.phases) {
            currentGameData.phases.add(p.getClientPhase());
        }
        for (PlayerSettings p : settings.playerSettings) {
            currentGameData.players.add(p.constructPlayer());
        }

        latestGameState = new MutableLiveData<>();
        currentPhaseNumber = new MutableLiveData<>();
        currentPhaseNumber.setValue(0);

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
        return currentGameData;
    }

    public int playerCount() {
        return getGameData().players.size();
    }

    public int getThisPlayer() {
        return thisPlayer;
    }
}
