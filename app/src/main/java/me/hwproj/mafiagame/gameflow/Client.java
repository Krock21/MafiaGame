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
    // a bunch of constant strings for broadcasts
//    public static final String GAME_STATE = "GAME_STATE";
//    public static final String SEND_GAME_STATE_EVENT = "SEND_GAME_STATE_EVENT";
    public static final String THIS_PHASE_NUMBER = "THIS_PHASE_NUMBER";

    // from activity
    public void sendPlayerAction(PlayerAction action) {
        if (sender != null) { // TODO
            sender.sendPlayerAction(action);
        }
    }
    // from activity
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

//    private static Context applicationContext; Shouldn't store context in static fields, it's a memory leak

    private static volatile Client singletonInstance;

    /**
     * First call constructs Client.
     * It is expected to happen from UI thread.
     * @param context actually unused
     * @return a reference to a singleton instance.
     */
    public static Client getClient(Context context) {
        return singletonInstance;
    }

    public static void ConstructClient(ClientSender sender, Settings settings) {
        singletonInstance = new Client(sender, settings);
    }

    private Client(ClientSender sender, Settings settings) {
        currentGameData = new ClientGameData();
        for (GamePhase p : settings.phases) {
            currentGameData.phases.add(p.getClientPhase());
        }
        currentGameData.nextPhase();
        for (int i = 0; i < settings.playerCount; i++) {
            currentGameData.players.add(new Player(null));
        }

        latestGameState = new MutableLiveData<>();
        currentPhaseNumber = new MutableLiveData<>();
        currentPhaseNumber.setValue(0);

        this.sender = sender;
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
}
