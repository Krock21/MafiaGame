package me.hwproj.mafiagame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.OnRealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateCallback;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateCallback;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import me.hwproj.mafiagame.networking.Network;
import me.hwproj.mafiagame.networking.NetworkData;

import static me.hwproj.mafiagame.networking.NetworkData.RC_INVITATION_INBOX;
import static me.hwproj.mafiagame.networking.NetworkData.RC_SELECT_PLAYERS;
import static me.hwproj.mafiagame.networking.NetworkData.RC_WAITING_ROOM;
import static me.hwproj.mafiagame.networking.NetworkData.mJoinedRoomConfig;
import static me.hwproj.mafiagame.networking.NetworkData.mRoom;
import static me.hwproj.mafiagame.networking.NetworkData.*;

/**
 * A room to configure a game instance, BEFORE connecting to clients.
 * Basically select minimum and maximum amounts of people in the game
 */
public class GameCreate extends AppCompatActivity {

    int minPlayerCount = 1; // minimum Player count other players
    int maxPlayerCount = 7; // maximum Player count other players

    private RoomUpdateCallback mRoomUpdateCallback = new MyRoomUpdateCallback(this);
    private RoomStatusUpdateCallback mRoomStatusCallbackHandler = new MyRoomStatusCallback(this);


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
    }

    private RealTimeMultiplayerClient makeRealTimeMultiplayerClient() {
        return Games.getRealTimeMultiplayerClient(this, NetworkData.getGoogleSignInAccount());
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
            mJoinedRoomConfig = roomBuilder.build();
            getRealTimeMultiplayerClient()
                    .create(mJoinedRoomConfig);

        }
    }
    void sendToAllReliably(byte[] message) {
        for (String participantId : mRoom.getParticipantIds()) {
            if (!participantId.equals(mMyParticipantId)) {
                Task<Integer> task = getRealTimeMultiplayerClient()
                        .sendReliableMessage(message, mRoom.getRoomId(), participantId,
                                handleMessageSentCallback).addOnCompleteListener(new OnCompleteListener<Integer>() {
                            @Override
                            public void onComplete(@NonNull Task<Integer> task) {
                                // Keep track of which messages are sent, if desired.
                                recordMessageToken(task.getResult());
                            }
                        });
            }
        }
    }
    HashSet<Integer> pendingMessageSet = new HashSet<>();


    synchronized void recordMessageToken(int tokenId) {
        pendingMessageSet.add(tokenId);
    }
    private RealTimeMultiplayerClient.ReliableMessageSentCallback handleMessageSentCallback =
            new RealTimeMultiplayerClient.ReliableMessageSentCallback() {
                @Override
                public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientId) {
                    // handle the message being sent.
                    synchronized (this) {
                        pendingMessageSet.remove(tokenId);
                    }
                }
            };

    private OnRealTimeMessageReceivedListener mMessageReceivedHandler =
            new OnRealTimeMessageReceivedListener() {
                @Override
                public void onRealTimeMessageReceived(@NonNull RealTimeMessage realTimeMessage) {
                    // Handle messages received here.
                    byte[] message = realTimeMessage.getMessageData();
                    // process message contents...
                    // VLAD TODO
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
}
