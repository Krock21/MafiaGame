package me.hwproj.mafiagame.gameinterface;

import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateCallback;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import me.hwproj.mafiagame.menu.MainActivity;
import me.hwproj.mafiagame.networking.NetworkData;

class MyRoomStatusCallback extends RoomStatusUpdateCallback {
    private final GameActivity activity;
    private final NetworkData networkData;

    public MyRoomStatusCallback(GameActivity activity, NetworkData networkData) {
        this.activity = activity;
        this.networkData = networkData;
    }
        @Override
        public void onRoomConnecting(@Nullable Room room) {
            networkData.setmRoom(room);
            if (room != null) {
                Log.d(MainActivity.TAG, "Room " + room.getRoomId() + " onRoomConnecting.");
            }
            // Update the UI status since we are in the process of connecting to a specific room. VLAD TODO
        }

        @Override
        public void onRoomAutoMatching(@Nullable Room room) {
            networkData.setmRoom(room);
            Log.d(MainActivity.TAG, "Room " + (room != null ? room.getRoomId() : null) + " onRoomAutoMatching.");
            // Update the UI status since we are in the process of matching other players. VLAD TODO
        }

        @Override
        public void onPeerInvitedToRoom(@Nullable Room room, @NonNull List<String> list) {
            networkData.setmRoom(room);
            Log.d(MainActivity.TAG, "Room " + (room != null ? room.getRoomId() : null) + " onPeerInvitedToRoom.");
            // Update the UI status since we are in the process of matching other players. VLAD TODO
        }

        @Override
        public void onPeerDeclined(@Nullable Room room, @NonNull List<String> list) {
            networkData.setmRoom(room);
            Log.d(MainActivity.TAG, "Room " + (room != null ? room.getRoomId() : null) + " onPeerDeclined.");
            // Peer declined invitation, see if game should be canceled
            if (!networkData.ismPlaying() && activity.shouldCancelGame(room)) {
                networkData.getRealTimeMultiplayerClient()
                        .leave(networkData.getmJoinedRoomConfig(), room.getRoomId());
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }

        @Override
        public void onPeerJoined(@Nullable Room room, @NonNull List<String> list) {
            networkData.setmRoom(room);

            activity.peersJoined(list); // Vlad
            Log.d(MainActivity.TAG, "Room " + (room != null ? room.getRoomId() : null) + " onPeerJoined.");
        }

        @Override
        public void onPeerLeft(@Nullable Room room, @NonNull List<String> list) {
            activity.peersLeft(list); // Vlad
            networkData.setmRoom(room);
            Log.d(MainActivity.TAG, "Room " + (room != null ? room.getRoomId() : null) + " onPeerLeft.");
            // Peer left, see if game should be canceled.
            if (!networkData.ismPlaying() && activity.shouldCancelGame(room)) {
                networkData.getRealTimeMultiplayerClient()
                        .leave(networkData.getmJoinedRoomConfig(), room.getRoomId());
                activity.roomCancelled(); // Vlad
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }

        @Override
        public void onConnectedToRoom(@Nullable Room room) {
            Log.d(MainActivity.TAG, "Room " + (room != null ? room.getRoomId() : null) + " onConnectedToRoom.");
            // Connected to room, record the room Id.
            networkData.setmRoom(room);
            Games.getPlayersClient(activity, NetworkData.getGoogleSignInAccount())
                    .getCurrentPlayerId().addOnSuccessListener(playerId -> networkData.setmMyParticipantId(networkData.getmRoom().getParticipantId(playerId)));
        }

        @Override
        public void onDisconnectedFromRoom(@Nullable Room room) {
            networkData.setmRoom(room);
            Log.d(MainActivity.TAG, "Room " + (room != null ? room.getRoomId() : null) + " onDisconnectedFromRoom.");
            // This usually happens due to a network error, leave the game.
            networkData.getRealTimeMultiplayerClient()
                    .leave(networkData.getmJoinedRoomConfig(), room.getRoomId());
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            // show error message and return to main screen
            networkData.setmRoom(null);
            networkData.setmJoinedRoomConfig(null);
        }

        @Override
        public void onPeersConnected(@Nullable Room room, @NonNull List<String> list) {
            networkData.setmRoom(room);
            Log.d(MainActivity.TAG, "Room " + (room != null ? room.getRoomId() : null) + " onPeersConnected.");
            if (networkData.ismPlaying()) {
                // add new player to an ongoing game
            } else if (activity.shouldStartGame(room)) {
                // start game! VLAD TODO what we have to do?
            }
        }

        @Override
        public void onPeersDisconnected(@Nullable Room room, @NonNull List<String> list) {
            networkData.setmRoom(room);
            Log.d(MainActivity.TAG, "Room " + (room != null ? room.getRoomId() : null) + " onPeerDeclined.");
            if (networkData.ismPlaying()) {
                // do game-specific handling of this -- remove player's avatar
                // from the screen, etc. If not enough players are left for
                // the game to go on, end the game and leave the room.
            } else if (activity.shouldCancelGame(room)) {
                // cancel the game
                networkData.getRealTimeMultiplayerClient()
                        .leave(networkData.getmJoinedRoomConfig(), room.getRoomId());
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
