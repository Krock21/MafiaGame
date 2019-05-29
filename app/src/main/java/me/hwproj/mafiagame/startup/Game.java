package me.hwproj.mafiagame.startup;

import android.util.Log;

import me.hwproj.mafiagame.GameConfigureFragment;
import me.hwproj.mafiagame.GameCreate;
import me.hwproj.mafiagame.MainActivity;
import me.hwproj.mafiagame.R;
import me.hwproj.mafiagame.gameflow.Settings;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.persistence.AppDatabaseInteractor;
import me.hwproj.mafiagame.persistence.DatabaseInteractor;

public class Game {

    private final GameCreate activity;

    private boolean isServer;
    private ServerGame serverGame; // present if isServer

    private GameConfigureFragment gameConfigureFragment; // present after roomFinished if isServer
    private ClientGame clientGame; // after roomFinished

    // TODO take only callbacks
    public Game(GameCreate activity) {
        this.activity = activity;
    }


    /**
     * Call this if this user created a room, becoming a server player
     */
    public void onStartRoom() {
        isServer = true;
        serverGame = new ServerGame(activity.senders.serverSender);

        activity.setServerCallback((participantId, message) -> {
            try {
                serverGame.receiveClientMessage(message, participantId);
            } catch (DeserializationException e) {
                e.printStackTrace();
            }
        });
    }

    public void onRoomFinished(int playerCount) {
        activity.setContentView(R.layout.activity_phase); // !!

//        if (isServer) {
//            initServer();
//            Log.d(MainActivity.TAG, "Server initialized");
//        }
        if (isServer) {
            gameConfigureFragment = GameConfigureFragment.newInstance(playerCount);
            activity.transactionProvider().add(R.id.fragmentLayout, gameConfigureFragment).commit();
        }

        initClient();
        Log.d(MainActivity.TAG, "Client initialized");

    }

    // called only if isServer
    public void onConfigureFinished(Settings settings) {
        if (isServer) {
            serverGame.initialize(settings);
            activity.transactionProvider().remove(gameConfigureFragment).commit();
        } else {
            Log.d("Bug", "onConfigureFinished: called then not isServer");
        }
    }

    private void initClient() {
        String name = new AppDatabaseInteractor(activity).loadName();

        clientGame = new ClientGame(activity.senders.clientSender, activity, activity::transactionProvider, name, this::onClientEnd);
        activity.setClientCallback(message -> {
            try {
                clientGame.receiveServerMessage(message);
            } catch (DeserializationException e) {
                Log.d("Bug", "startMultiplayerGame: ...");
                e.printStackTrace();
            }
        });
        clientGame.sendInitRequest();
    }

    // callback for ClientGame
    private void onClientEnd() {
        // stop receiving client packages
        activity.setClientCallback(null);
    }
}
