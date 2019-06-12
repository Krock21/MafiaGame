package me.hwproj.mafiagame.gameinterface;

import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateCallback;

import me.hwproj.mafiagame.menu.MainActivity;
import me.hwproj.mafiagame.networking.NetworkData;

class MyRoomUpdateCallback extends RoomUpdateCallback {
    private final GameActivity activity;
    private final NetworkData networkData;

    public MyRoomUpdateCallback(GameActivity activity, NetworkData networkData) {
        this.activity = activity;
        this.networkData = networkData;
    }

    @Override
    public void onRoomCreated(int code, @Nullable Room room) {
        // Update UI and internal state based on room updates. VLAD TODO
        networkData.setmRoom(room);
        if (code == GamesCallbackStatusCodes.OK && room != null) {
            Log.d(MainActivity.TAG, "Room " + room.getRoomId() + " created.");

            activity.onStartRoom(); // we are the server

            activity.showWaitingRoom(room, activity.maxPlayerCount + 1);
        } else {
            Log.w(MainActivity.TAG, "Error creating room: " + code);
            // let screen go to sleep
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onJoinedRoom(int code, @Nullable Room room) {
        // Update UI and internal state based on room updates. VLAD TODO
        networkData.setmRoom(room);
        if (code == GamesCallbackStatusCodes.OK && room != null) {
            Log.d(MainActivity.TAG, "Room " + room.getRoomId() + " joined.");
            activity.showWaitingRoom(room, activity.maxPlayerCount + 1);
        } else {
            Log.w(MainActivity.TAG, "Error joining room: " + code);
            // let screen go to sleep
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onLeftRoom(int code, @NonNull String roomId) {
        Log.d(MainActivity.TAG, "Left room" + roomId);
    }

    @Override
    public void onRoomConnected(int code, @Nullable Room room) {
        networkData.setmRoom(room);
        if (code == GamesCallbackStatusCodes.OK && room != null) {
            Log.d(MainActivity.TAG, "Room " + room.getRoomId() + " connected.");
        } else {
            Log.w(MainActivity.TAG, "Error connecting to room: " + code);
            // let screen go to sleep
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
}
