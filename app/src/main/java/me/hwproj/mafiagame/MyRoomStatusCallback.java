package me.hwproj.mafiagame;

import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateCallback;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import me.hwproj.mafiagame.networking.NetworkData;

import static me.hwproj.mafiagame.networking.NetworkData.getGoogleSignInAccount;
import static me.hwproj.mafiagame.networking.NetworkData.getRealTimeMultiplayerClient;
import static me.hwproj.mafiagame.networking.NetworkData.mJoinedRoomConfig;
import static me.hwproj.mafiagame.networking.NetworkData.mMyParticipantId;
import static me.hwproj.mafiagame.networking.NetworkData.mPlaying;
import static me.hwproj.mafiagame.networking.NetworkData.mRoom;

class MyRoomStatusCallback extends RoomStatusUpdateCallback {
    private GameCreate activity;

    public MyRoomStatusCallback(GameCreate activity) {
        this.activity = activity;
    }
        @Override
        public void onRoomConnecting(@Nullable Room room) {
            NetworkData.mRoom = room;
            Log.d(MainActivity.TAG, "Room " + room.getRoomId() + " onRoomConnecting.");
            // Update the UI status since we are in the process of connecting to a specific room. VLAD TODO
        }

        @Override
        public void onRoomAutoMatching(@Nullable Room room) {
            NetworkData.mRoom = room;
            Log.d(MainActivity.TAG, "Room " + room.getRoomId() + " onRoomAutoMatching.");
            // Update the UI status since we are in the process of matching other players. VLAD TODO
        }

        @Override
        public void onPeerInvitedToRoom(@Nullable Room room, @NonNull List<String> list) {
            NetworkData.mRoom = room;
            Log.d(MainActivity.TAG, "Room " + room.getRoomId() + " onPeerInvitedToRoom.");
            // Update the UI status since we are in the process of matching other players. VLAD TODO
        }

        @Override
        public void onPeerDeclined(@Nullable Room room, @NonNull List<String> list) {
            NetworkData.mRoom = room;
            Log.d(MainActivity.TAG, "Room " + room.getRoomId() + " onPeerDeclined.");
            // Peer declined invitation, see if game should be canceled
            if (!mPlaying && activity.shouldCancelGame(room)) {
                NetworkData.getRealTimeMultiplayerClient()
                        .leave(mJoinedRoomConfig, room.getRoomId());
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }

        @Override
        public void onPeerJoined(@Nullable Room room, @NonNull List<String> list) {
            NetworkData.mRoom = room;
            // Update UI status indicating new players have joined! VLAD TODO
            Log.d(MainActivity.TAG, "Room " + room.getRoomId() + " onPeerJoined.");
        }

        @Override
        public void onPeerLeft(@Nullable Room room, @NonNull List<String> list) {
            NetworkData.mRoom = room;
            Log.d(MainActivity.TAG, "Room " + room.getRoomId() + " onPeerLeft.");
            // Peer left, see if game should be canceled.
            if (!mPlaying && activity.shouldCancelGame(room)) {
                NetworkData.getRealTimeMultiplayerClient()
                        .leave(mJoinedRoomConfig, room.getRoomId());
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }

        @Override
        public void onConnectedToRoom(@Nullable Room room) {
            NetworkData.mRoom = room;
            Log.d(MainActivity.TAG, "Room " + room.getRoomId() + " onConnectedToRoom.");
            // Connected to room, record the room Id.
            mRoom = room;
            Games.getPlayersClient(activity, getGoogleSignInAccount())
                    .getCurrentPlayerId().addOnSuccessListener(new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String playerId) {
                    mMyParticipantId = mRoom.getParticipantId(playerId);
                }
            });
        }

        @Override
        public void onDisconnectedFromRoom(@Nullable Room room) {
            NetworkData.mRoom = room;
            Log.d(MainActivity.TAG, "Room " + room.getRoomId() + " onDisconnectedFromRoom.");
            // This usually happens due to a network error, leave the game.
            getRealTimeMultiplayerClient()
                    .leave(mJoinedRoomConfig, room.getRoomId());
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            // show error message and return to main screen
            mRoom = null;
            mJoinedRoomConfig = null;
        }

        @Override
        public void onPeersConnected(@Nullable Room room, @NonNull List<String> list) {
            NetworkData.mRoom = room;
            Log.d(MainActivity.TAG, "Room " + room.getRoomId() + " onPeersConnected.");
            if (mPlaying) {
                // add new player to an ongoing game
            } else if (activity.shouldStartGame(room)) {
                // start game! VLAD TODO what we have to do?
            }
        }

        @Override
        public void onPeersDisconnected(@Nullable Room room, @NonNull List<String> list) {
            NetworkData.mRoom = room;
            Log.d(MainActivity.TAG, "Room " + room.getRoomId() + " onPeerDeclined.");
            if (mPlaying) {
                // do game-specific handling of this -- remove player's avatar
                // from the screen, etc. If not enough players are left for
                // the game to go on, end the game and leave the room.
            } else if (activity.shouldCancelGame(room)) {
                // cancel the game
                getRealTimeMultiplayerClient()
                        .leave(mJoinedRoomConfig, room.getRoomId());
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }

        @Override
        public void onP2PConnected(@NonNull String participantId) {
            // Update status due to new peer to peer connection.
        }

        @Override
        public void onP2PDisconnected(@NonNull String participantId) {
            // Update status due to peer to peer connection being disconnected.
        }
}