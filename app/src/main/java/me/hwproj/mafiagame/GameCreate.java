package me.hwproj.mafiagame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
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
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateCallback;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateCallback;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;

import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.networking.messaging.Callbacks;
import me.hwproj.mafiagame.networking.NetworkData;
import me.hwproj.mafiagame.networking.messaging.ClientCallback;
import me.hwproj.mafiagame.networking.messaging.Senders;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import me.hwproj.mafiagame.gameflow.Settings;
import me.hwproj.mafiagame.networking.messaging.ServerCallback;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.startup.ClientGame;
import me.hwproj.mafiagame.startup.Game;
import me.hwproj.mafiagame.startup.ServerGame;

import static me.hwproj.mafiagame.networking.NetworkData.*;


/**
 * A room to configure a game instance, BEFORE connecting to clients.
 * Basically select minimum and maximum amounts of people in the game
 */
public class GameCreate extends AppCompatActivity implements GameConfigureFragment.ConfigurationCompleteListener {

    public boolean mWaitingRoomFinishedFromCode = false;

    public int minPlayerCount = 1; // minimum Player count other players
    public int maxPlayerCount = 7; // maximum Player count other players

    public final Senders senders = new Senders(this);
    public final Callbacks callbacks = new Callbacks();
    public final RoomUpdateCallback mRoomUpdateCallback = new MyRoomUpdateCallback(this);
    public final RoomStatusUpdateCallback mRoomStatusCallbackHandler = new MyRoomStatusCallback(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_create);
        NetworkData.setRealTimeMultiplayerClient(makeRealTimeMultiplayerClient());

        Button config = findViewById(R.id.configure);
        config.setOnClickListener(v -> {
            minPlayerCount = 1;
            maxPlayerCount = 7;
        });

        Button getRealTimeMultiplayerClient = findViewById(R.id.getRealTimeMultiplayerClient);
        getRealTimeMultiplayerClient.setOnClickListener(v -> {
            NetworkData.setRealTimeMultiplayerClient(makeRealTimeMultiplayerClient());
        });

        Button makeRoom = findViewById(R.id.makeRoom); // a button for you
        makeRoom.setOnClickListener(v -> {
            //startActivity(new Intent(this, PhaseActivity.class));
            invitePlayers(minPlayerCount, maxPlayerCount);
        });

        Button checkForInvitation = findViewById(R.id.checkForInvitation);
        checkForInvitation.setOnClickListener(v -> {
            checkForInvitation();
        });

        Button invitationInbox = findViewById(R.id.invitationInbox);
        invitationInbox.setOnClickListener(v -> {
            showInvitationInbox();
        });

        Button start = findViewById(R.id.test_run);
//        start.setOnClickListener(v -> {
//            if (isServer) {
//                serverGame.initialize();
//            }
//        });
        game = new Game(this);
    }

    private RealTimeMultiplayerClient makeRealTimeMultiplayerClient() {
        return Games.getRealTimeMultiplayerClient(this, getGoogleSignInAccount());
    }

    private void invitePlayers(int minPlayerCount, int maxPlayerCount) {
        // launch the player selection screen
        // minimum: minPlayerCount other player; maximum: maxPlayerCount other players
        if (NetworkData.getRealTimeMultiplayerClient() != null) {
            NetworkData.getRealTimeMultiplayerClient()
                    .getSelectOpponentsIntent(minPlayerCount, maxPlayerCount, true)
                    .addOnSuccessListener(new OnSuccessListener<Intent>() {
                        @Override
                        public void onSuccess(Intent intent) {
                            startActivityForResult(intent, RC_SELECT_PLAYERS);
                        }
                    });
        } else {
            new AlertDialog.Builder(this).setMessage("You Should get RealTimeMultiplayerClient at first")
                    .setNeutralButton(android.R.string.ok, null).show();
        }
    }

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
            setmJoinedRoomConfig(roomBuilder.build());
            getRealTimeMultiplayerClient()
                    .create(getmJoinedRoomConfig());
        }
        if (requestCode == RC_WAITING_ROOM) {

            // Look for finishing the waiting room from code, for example if a
            // "start game" message is received.  In this case, ignore the result. TODO Start Game received
            if (mWaitingRoomFinishedFromCode) {
                Log.d("START", "calling onRoomFinished from higher if");
                onRoomFinished(); // Vlad
                // TODO idk if it really needs to be here
                return;
            }

            if (resultCode == Activity.RESULT_OK) {
                // Start the game!
                Log.d("START", "calling onRoomFinished from lower if");
                onRoomFinished(); // Vlad
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Waiting room was dismissed with the back button. The meaning of this
                // action is up to the game. You may choose to leave the room and cancel the
                // match, or do something else like minimize the waiting room and
                // continue to connect in the background.

                // in this example, we take the simple approach and just leave the room:
                getRealTimeMultiplayerClient()
                        .leave(getmJoinedRoomConfig(), getmRoom().getRoomId());
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                // player wants to leave the room.
                getRealTimeMultiplayerClient()
                        .leave(getmJoinedRoomConfig(), getmRoom().getRoomId());
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
            Invitation invitation = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);
            if (invitation != null) {
                RoomConfig.Builder builder = RoomConfig.builder(mRoomUpdateCallback)
                        .setInvitationIdToAccept(invitation.getInvitationId())
                        .setOnMessageReceivedListener(mMessageReceivedHandler)
                        .setRoomStatusUpdateCallback(mRoomStatusCallbackHandler);
                setmJoinedRoomConfig(builder.build());
                getRealTimeMultiplayerClient()
                        .join(getmJoinedRoomConfig());
                // prevent screen from sleeping during handshake
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }
    }

    public void messageReceived(String ParticipantId, byte[] message) {
        Log.d(MainActivity.TAG, "MESSAGE RECIEVED FROM: " + ParticipantId + ", msg: " + Arrays.toString(message));
        if (message[0] == (byte) 0) { // to client
            message = Senders.removeFromBegin(message);
            if (callbacks.clientCallback != null)
                callbacks.clientCallback.receiveServerMessage(message);
        } else if (message[0] == (byte) 1) { // to server
            message = Senders.removeFromBegin(message);
            if (callbacks.serverCallback != null)
                callbacks.serverCallback.receiveClientMessage(ParticipantId, message);
        } else {
            Log.e(MainActivity.TAG, "message's first byte isn't 0 or 1");
        }
    }

    private OnRealTimeMessageReceivedListener mMessageReceivedHandler =
            new OnRealTimeMessageReceivedListener() {
                @Override
                public void onRealTimeMessageReceived(@NonNull RealTimeMessage realTimeMessage) {
                    // Handle messages received here.
                    byte[] message = realTimeMessage.getMessageData();
                    // process message contents...
                    messageReceived(realTimeMessage.getSenderParticipantId(), message);
                }
            };


    // returns whether there are enough players to start the game

    boolean shouldStartGame(Room room) {
        int connectedPlayers = 0;
        for (Participant p : room.getParticipants()) {
            if (p.isConnectedToRoom()) {
                ++connectedPlayers;
            }
        }
        return connectedPlayers >= MIN_PLAYERS;
    }
    // Returns whether the room is in a state where the game should be canceled.

    boolean shouldCancelGame(Room room) {
        // TODO: Your game-specific cancellation logic here. For example, you might decide to
        // cancel the game if enough people have declined the invitation or left the room.
        // You can check a participant's status with Participant.getStatus().
        // (Also, your UI should have a Cancel button that cancels the game too)
        return false;
    }

    public void showWaitingRoom(Room room, int maxPlayersToStartGame) {
        Log.d(MainActivity.TAG, "showing Waiting Room");
        getRealTimeMultiplayerClient()
                .getWaitingRoomIntent(room, maxPlayersToStartGame)
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_WAITING_ROOM);
                    }
                });
    }

    public void checkForInvitation() {
        Games.getGamesClient(this, getGoogleSignInAccount())
                .getActivationHint()
                .addOnSuccessListener(
                        new OnSuccessListener<Bundle>() {
                            @Override
                            public void onSuccess(Bundle bundle) {
                                Invitation invitation = bundle.getParcelable(Multiplayer.EXTRA_INVITATION);
                                if (invitation != null) {
                                    RoomConfig.Builder builder = RoomConfig.builder(mRoomUpdateCallback)
                                            .setInvitationIdToAccept(invitation.getInvitationId())
                                            .setOnMessageReceivedListener(mMessageReceivedHandler)
                                            .setRoomStatusUpdateCallback(mRoomStatusCallbackHandler);
                                    setmJoinedRoomConfig(builder.build());
                                    getRealTimeMultiplayerClient()
                                            .join(getmJoinedRoomConfig());
                                    // prevent screen from sleeping during handshake
                                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                }
                            }
                        }
                );

    }

    private void showInvitationInbox() {
        Games.getInvitationsClient(this, getGoogleSignInAccount())
                .getInvitationInboxIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_INVITATION_INBOX);
                    }
                });
    }

    // --------------------- for client -----------------------

    public FragmentTransaction transactionProvider() {
        return getSupportFragmentManager().beginTransaction();
    }

    // ------------- interface for room callbacks -------------

    Game game;

    private int peerCount = 0;

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

    public void onRoomFinished() {
        Log.d(MainActivity.TAG, "Room finished called");
        game.onRoomFinished(1 + peerCount); // peer count does not include server device
    }

    // ------------- for ConfigurationFragment --------------

    @Override
    public void onConfigurationFinished(Settings settings) {
        game.onConfigureFinished(settings);
    }

    // ----------------- interface for Game -----------------

    public void setServerCallback(ServerCallback callback) {
        callbacks.serverCallback = callback;
    }

    public void setClientCallback(ClientCallback callback) {
        callbacks.clientCallback = callback;
    }
}
