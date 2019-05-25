package me.hwproj.mafiagame;

import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateCallback;

import me.hwproj.mafiagame.networking.NetworkData;

class MyRoomUpdateCallback extends RoomUpdateCallback {
    private GameCreate activity;

    public MyRoomUpdateCallback(GameCreate activity) {
        this.activity = activity;
    }

    @Override
    public void onRoomCreated(int code, @Nullable Room room) {
        // Update UI and internal state based on room updates. VLAD TODO
        NetworkData.mRoom = room;
        if (code == GamesCallbackStatusCodes.OK && room != null) {
            Log.d(MainActivity.TAG, "Room " + room.getRoomId() + " created.");
        } else {
            Log.w(MainActivity.TAG, "Error creating room: " + code);
            // let screen go to sleep
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onJoinedRoom(int code, @Nullable Room room) {
        // Update UI and internal state based on room updates. VLAD TODO
        NetworkData.mRoom = room;
        if (code == GamesCallbackStatusCodes.OK && room != null) {
            Log.d(MainActivity.TAG, "Room " + room.getRoomId() + " joined.");
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
        NetworkData.mRoom = room;
        if (code == GamesCallbackStatusCodes.OK && room != null) {
            Log.d(MainActivity.TAG, "Room " + room.getRoomId() + " connected.");
        } else {
            Log.w(MainActivity.TAG, "Error connecting to room: " + code);
            // let screen go to sleep
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
}
