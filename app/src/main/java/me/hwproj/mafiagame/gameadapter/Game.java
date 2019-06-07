package me.hwproj.mafiagame.gameadapter;

import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import me.hwproj.mafiagame.gameinterface.GameConfigureFragment;
import me.hwproj.mafiagame.gameinterface.GameActivity;
import me.hwproj.mafiagame.gameinterface.DeadWaitFragment;
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

    /**
     * Constructs a new Game instance linked to a provided GameActivity
     * @param activity activity to display game in
     */
    public Game(GameActivity activity) {
        this.activity = activity;
    }


    /**
     * Called if this user created a room, becoming a server player
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

    /**
     * Called when a room is completed and no new players will connect.
     * @param playerCount number of players in the game
     */
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

    /**
     * Called when game settings are determined and a game
     * should be started with them.
     * Called only on server device
     * @param settings setting to initialize a server with
     */
    public void onConfigureFinished(Settings settings) {
        if (isServer) {
            serverGame.initialize(settings);
            activity.transactionProvider().remove(gameConfigureFragment).commit();
        } else {
            Log.d("Bug", "onConfigureFinished: called then not isServer");
        }
    }

    /**
     * Returns whether this game in somewhat initialized and should not be lost
     * @return if game in started in some way
     */
    public boolean isStarted() {
        return isServer || (clientGame != null);
    }

    /**
     * Constructs a {@link ClientGame} and sends initialization request for it
     */
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

    /**
     * What to do when client is no longer participates in the game.
     * This method is a callback for ClientGame
     */
    private void onClientEnd() {
        // stop receiving client packages
        activity.setClientCallback(null);
        // and stop sending packages
        activity.senders.clientSender = null;

        String showText;
        String toolbarText;

        // don't ruin server
        if (isServer) {
            showText = activity.getString(R.string.your_device_runs_server);
            toolbarText = activity.getString(R.string.server_wait_toolbar);
        } else {
            showText = activity.getString(R.string.gg);
            toolbarText = activity.getString(R.string.client_wait_toolbar);
        }


        activity.transactionProvider()
                .add(R.id.fragmentLayout, new DeadWaitFragment(showText, !isServer))
                .commit();
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        TextView toolbarTextView = toolbar.findViewById(R.id.toolbarTextView);
        toolbarTextView.setText(toolbarText);
    }
}
