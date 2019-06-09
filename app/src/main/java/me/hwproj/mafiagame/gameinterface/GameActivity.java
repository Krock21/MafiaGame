package me.hwproj.mafiagame.gameinterface;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.OnRealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateCallback;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateCallback;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;

import me.hwproj.mafiagame.R;
import me.hwproj.mafiagame.menu.MainActivity;
import me.hwproj.mafiagame.networking.messaging.NetworkCallbacks;
import me.hwproj.mafiagame.networking.NetworkData;
import me.hwproj.mafiagame.networking.messaging.ClientNetworkCallback;
import me.hwproj.mafiagame.networking.messaging.Senders;

import java.util.List;

import me.hwproj.mafiagame.gameflow.Settings;
import me.hwproj.mafiagame.networking.messaging.ServerNetworkCallback;
import me.hwproj.mafiagame.gameadapter.Game;

import static me.hwproj.mafiagame.networking.NetworkData.*;


/**
 * An activity to create/join game and to play it.
 *
 * All of this must be in the same activity because connection
 * to the multiplayer room cannot be passed between activities.
 */
public class GameActivity extends AppCompatActivity implements GameConfigureFragment.ConfigurationCompleteListener {

    private final NetworkData networkData = new NetworkData();

    /**
     * Minimal allowed number of other players in the room.
     */
    private final int minPlayerCount = 1;

    /**
     * Maximal allowed number of other players in the room.
     * It is public because {@link MyRoomStatusCallback} needs access to it.
     */
    public final int maxPlayerCount = 7;

    /**
     * Network senders for game to use
     */
    public final Senders senders = new Senders(this, networkData);
    private final NetworkCallbacks networkCallbacks = new NetworkCallbacks();
    private final RoomUpdateCallback mRoomUpdateCallback = new MyRoomUpdateCallback(this, networkData);
    private final RoomStatusUpdateCallback mRoomStatusCallbackHandler = new MyRoomStatusCallback(this, networkData);

    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_create);
        networkData.setRealTimeMultiplayerClient(makeRealTimeMultiplayerClient());

        Button makeRoom = findViewById(R.id.makeRoom);
        makeRoom.setOnClickListener(v -> invitePlayers(minPlayerCount, maxPlayerCount));

        Button invitationInbox = findViewById(R.id.invitationInbox);
        invitationInbox.setOnClickListener(v -> showInvitationInbox());

        game = new Game(this);
    }

    /**
     * What to do when back button pressed.
     * When game is started, going back is forbidden.
     */
    @Override
    public void onBackPressed() {
        if (game.isStarted()) {
            // forbid going back once the game is started
            return;
        }
        super.onBackPressed();
    }

    /**
     * Constructs a multiplayer client
     * @return a newly constructed multiplayer client
     */
    private RealTimeMultiplayerClient makeRealTimeMultiplayerClient() {
        return Games.getRealTimeMultiplayerClient(this, getGoogleSignInAccount());
    }

    /**
     * Launches player invitation screen.
     * @param minPlayerCount min players invited
     * @param maxPlayerCount max players invited
     */
    private void invitePlayers(int minPlayerCount, int maxPlayerCount) {
        // launch the player selection screen
        // minimum: minPlayerCount other player; maximum: maxPlayerCount other players
        if (networkData.getRealTimeMultiplayerClient() != null) {
            networkData.getRealTimeMultiplayerClient()
                    .getSelectOpponentsIntent(minPlayerCount, maxPlayerCount, true)
                    .addOnSuccessListener(intent -> startActivityForResult(intent, RC_SELECT_PLAYERS));
        } else {
            new AlertDialog.Builder(this).setMessage("You Should get RealTimeMultiplayerClient at first")
                    .setNeutralButton(android.R.string.ok, null).show();
        }
    }

    /**
     * Here used to get results of google's activities for room management
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SELECT_PLAYERS) {
            if (resultCode != Activity.RESULT_OK) {
                new AlertDialog.Builder(this).setMessage("Error in creating room")
                        .setNeutralButton(android.R.string.ok, null).show();
                return;
            }

            // Get the invitee list.
            assert data != null;
            final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            // Get Automatch criteria.
            int minAutoPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

            // Create the room configuration.
            RoomConfig.Builder roomBuilder = RoomConfig.builder(mRoomUpdateCallback)
                    .setOnMessageReceivedListener(mMessageReceivedHandler)
                    .setRoomStatusUpdateCallback(mRoomStatusCallbackHandler)
                    .addPlayersToInvite(invitees);
            if (minAutoPlayers > 0) {
                roomBuilder.setAutoMatchCriteria(
                        RoomConfig.createAutoMatchCriteria(minAutoPlayers, maxAutoPlayers, 0));
            }

            // Save the roomConfig so we can use it if we call leave().
            networkData.setmJoinedRoomConfig(roomBuilder.build());
            networkData.getRealTimeMultiplayerClient()
                    .create(networkData.getmJoinedRoomConfig());
        }
        if (requestCode == RC_WAITING_ROOM) {

            if (resultCode == Activity.RESULT_OK) {
                // Start the game!
                Log.d("START", "calling onRoomFinished from lower if");
                onRoomFinished(); // Vlad
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Waiting room was dismissed with the back button. The meaning of this
                // action is up to the game. You may choose to leave the room and cancel the
                // match, or do something else like minimize the waiting room and
                // continue to connect in the background.

                // just leave the room:
                networkData.getRealTimeMultiplayerClient()
                        .leave(networkData.getmJoinedRoomConfig(), networkData.getmRoom().getRoomId());
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                // player wants to leave the room.
                networkData.getRealTimeMultiplayerClient()
                        .leave(networkData.getmJoinedRoomConfig(), networkData.getmRoom().getRoomId());
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }
        if (requestCode == RC_INVITATION_INBOX) {
            Log.d(MainActivity.TAG, "RC_INVITATION_INBOX");
            if (resultCode != Activity.RESULT_OK) {
                // Canceled or some error.
                return;
            }
            Log.d(MainActivity.TAG, "RC_INVITATION_INBOX is OK");

            if (data == null || data.getExtras() == null) {
                // We think this things are never null
                Log.d("Bug", "onActivityResult: data = " + data + ", something is null");
                return;
            }

            Invitation invitation = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);
            if (invitation != null) {
                RoomConfig.Builder builder = RoomConfig.builder(mRoomUpdateCallback)
                        .setInvitationIdToAccept(invitation.getInvitationId())
                        .setOnMessageReceivedListener(mMessageReceivedHandler)
                        .setRoomStatusUpdateCallback(mRoomStatusCallbackHandler);
                networkData.setmJoinedRoomConfig(builder.build());
                networkData.getRealTimeMultiplayerClient()
                        .join(networkData.getmJoinedRoomConfig());
                // prevent screen from sleeping during handshake
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }
    }

    /**
     * Called when a message received from any player
     * @param ParticipantId who send this message
     * @param message       message contents
     */
    public void messageReceived(String ParticipantId, byte[] message) {
        Log.d(MainActivity.TAG, "MESSAGE RECEIVED FROM: " + ParticipantId + ", msg: " + Arrays.toString(message));
        if (message[0] == (byte) 0) { // to client
            message = Senders.removeFromBegin(message);
            if (networkCallbacks.clientCallback != null)
                networkCallbacks.clientCallback.receiveServerMessage(message);
        } else if (message[0] == (byte) 1) { // to server
            message = Senders.removeFromBegin(message);
            if (networkCallbacks.serverCallback != null)
                networkCallbacks.serverCallback.receiveClientMessage(ParticipantId, message);
        } else {
            Log.e(MainActivity.TAG, "message's first byte isn't 0 or 1");
        }
    }

    /**
     * That's what actually receives messages
     */
    private final OnRealTimeMessageReceivedListener mMessageReceivedHandler =
            realTimeMessage -> {
                // Handle messages received here.
                byte[] message = realTimeMessage.getMessageData();
                // process message contents...
                messageReceived(realTimeMessage.getSenderParticipantId(), message);
            };


    // returns whether there are enough players to start the game

    /**
     * Decides whether room is ready to start the game
     * @param room room to check
     * @return if game should be started with provided room
     */
    boolean shouldStartGame(Room room) {
        int connectedPlayers = 0;
        for (Participant p : room.getParticipants()) {
            if (p.isConnectedToRoom()) {
                ++connectedPlayers;
            }
        }
        // minPlayerCount does not include host
        return connectedPlayers >= minPlayerCount + 1;
    }

    /**
     * This method determines whether it is impossible to start a game in
     * a provided room and it should be cancelled
     *
     * Currently always returns <code>false</code>
     *
     * @param room room to check
     * @return if to room should be cancelled automatically
     */
    @SuppressWarnings("unused")
    boolean shouldCancelGame(Room room) {
        // TODO
        // actually player can himself decide that a room should be cancelled,
        // so it is not very important
        return false;
    }

    /**
     * Shows an activity in which player waits for other players to connect
     * @param room                  where other players are connecting
     * @param maxPlayersToStartGame how many players are expected
     */
    public void showWaitingRoom(Room room, int maxPlayersToStartGame) {
        Log.d(MainActivity.TAG, "showing Waiting Room");
        networkData.getRealTimeMultiplayerClient()
                .getWaitingRoomIntent(room, maxPlayersToStartGame)
                .addOnSuccessListener(intent -> startActivityForResult(intent, RC_WAITING_ROOM));
    }

    /**
     * Shows an activity where a player can look at his invitations and accept them
     */
    private void showInvitationInbox() {
        Games.getInvitationsClient(this, getGoogleSignInAccount())
                .getInvitationInboxIntent()
                .addOnSuccessListener(intent -> startActivityForResult(intent, RC_INVITATION_INBOX));
    }

    // --------------------- for client -----------------------

    /**
     * Generates a new {@link FragmentTransaction} for this activity.
     * This method is given to {@link me.hwproj.mafiagame.gameadapter.ClientGame} so that
     * it could change fragments in this activity
     * @return FragmentTransaction for this activity
     */
    public FragmentTransaction transactionProvider() {
        return getSupportFragmentManager().beginTransaction();
    }

    // ------------- interface for room networkCallbacks -------------

    private int peerCount = 0;

    // this methods are called by networkCallbacks and handle various
    // multiplayer room changes

    public void peersJoined(List<String> list) {
        Log.d(MainActivity.TAG, "peersJoined: " + list);
        peerCount += list.size();
    }

    public void peersLeft(List<String> list) {
        Log.d(MainActivity.TAG, "peersLeft: " + list);
        peerCount -= list.size();
    }

    public void roomCancelled() {
        Log.d(MainActivity.TAG, "Room cancelled called");
        peerCount = 0;
        game = new Game(this);
    }

    public void onStartRoom() {
        Log.d(MainActivity.TAG, "Room started called");
        game.onStartRoom();
    }

    private void onRoomFinished() {
        Log.d(MainActivity.TAG, "Room finished called");
        game.onRoomFinished(1 + peerCount); // peer count does not include server device
    }

    // ------------- for ConfigurationFragment --------------

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConfigurationFinished(Settings settings) {
        game.onConfigureFinished(settings);
    }

    // ----------------- interface for Game -----------------

    /**
     * Sets {@link ServerNetworkCallback} to use
     * @param callback callback to use
     */
    public void setServerCallback(ServerNetworkCallback callback) {
        networkCallbacks.serverCallback = callback;
    }

    /**
     * Sets {@link ClientNetworkCallback} to use
     * @param callback callback to use
     */
    public void setClientCallback(ClientNetworkCallback callback) {
        networkCallbacks.clientCallback = callback;
    }
}
