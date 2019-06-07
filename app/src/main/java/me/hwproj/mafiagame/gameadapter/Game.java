package me.hwproj.mafiagame.gameadapter;

import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import me.hwproj.mafiagame.gameinterface.GameConfigureFragment;
import me.hwproj.mafiagame.gameinterface.GameActivity;
import me.hwproj.mafiagame.gameinterface.ServerWaitFragment;
import me.hwproj.mafiagame.menu.MainActivity;
import me.hwproj.mafiagame.R;
import me.hwproj.mafiagame.gameflow.Settings;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.persistence.AppDatabaseInteractor;

/**
 * Represents a game of mafia on any stage from not initialized
 * to the end.
 */
public class Game {

    private final GameActivity activity;

    private boolean isServer;
    private ServerGame serverGame; // present if isServer

    private GameConfigureFragment gameConfigureFragment; // present after roomFinished if isServer
    private ClientGame clientGame; // after roomFinished

    // TODO take only callbacks
    public Game(GameActivity activity) {
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
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        TextView toolbarText = toolbar.findViewById(R.id.toolbarTextView);

        if (isServer) {
            gameConfigureFragment = GameConfigureFragment.newInstance(playerCount);
            activity.transactionProvider().add(R.id.fragmentLayout, gameConfigureFragment).commit();
            toolbarText.setText(activity.getString(R.string.toolbar_configure_game));
        } else {
            toolbarText.setText(activity.getString(R.string.toolbar_wait_for_configure));
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

    public boolean isStarted() {
        return isServer || (clientGame != null);
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
        // and stop sending packages
        activity.senders.clientSender = null;

        // don't ruin server
        if (!isServer) {
            activity.startActivity(new Intent(activity, MainActivity.class));
        } else {
            activity.transactionProvider().add(R.id.fragmentLayout, new ServerWaitFragment()).commit();
            
            Toolbar toolbar = activity.findViewById(R.id.toolbar);
            TextView toolbarText = toolbar.findViewById(R.id.toolbarTextView);
            toolbarText.setText(activity.getString(R.string.server_wait_toolbar));
        }

    }
}
