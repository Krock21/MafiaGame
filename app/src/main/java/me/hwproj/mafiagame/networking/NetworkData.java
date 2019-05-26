package me.hwproj.mafiagame.networking;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NetworkData {
    public static final int RC_GAMES_SIGN_IN = 9001;
    public static final int RC_SELECT_PLAYERS = 9006;
    public static final int RC_WAITING_ROOM = 9007;
    public static final int RC_INVITATION_INBOX = 9008;
    private static Lock mJoinedRoomConfigLock = new ReentrantLock();
    private static RoomConfig mJoinedRoomConfig;
    private static Lock mMyParticipantIdLock = new ReentrantLock();
    private static String mMyParticipantId;
    private static Lock mRoomLock = new ReentrantLock();
    private static Room mRoom;
    private static Lock googleSignInAccountLock = new ReentrantLock();
    private static GoogleSignInAccount googleSignInAccount;
    private static Lock realTimeMultiplayerClientLock = new ReentrantLock();
    private static RealTimeMultiplayerClient realTimeMultiplayerClient;

    // at least 2 players required for our game
    public final static int MIN_PLAYERS = 2;

    // are we already playing?
    private static Lock mPlayingLock = new ReentrantLock();
    private static boolean mPlaying = false;

    public static void setGoogleSignInAccount(GoogleSignInAccount googleSignInAccount) {
        googleSignInAccountLock.lock();
        NetworkData.googleSignInAccount = googleSignInAccount;
        googleSignInAccountLock.unlock();
    }

    public static GoogleSignInAccount getGoogleSignInAccount() {
        googleSignInAccountLock.lock();
        GoogleSignInAccount answer = googleSignInAccount;
        googleSignInAccountLock.unlock();
        return answer;
    }

    public static RealTimeMultiplayerClient getRealTimeMultiplayerClient() {
        realTimeMultiplayerClientLock.lock();
        RealTimeMultiplayerClient answer = realTimeMultiplayerClient;
        realTimeMultiplayerClientLock.unlock();
        return answer;
    }

    public static void setRealTimeMultiplayerClient(RealTimeMultiplayerClient realTimeMultiplayerClient) {
        realTimeMultiplayerClientLock.lock();
        NetworkData.realTimeMultiplayerClient = realTimeMultiplayerClient;
        realTimeMultiplayerClientLock.unlock();
    }

    public static RoomConfig getmJoinedRoomConfig() {
        mJoinedRoomConfigLock.lock();
        RoomConfig answer = mJoinedRoomConfig;
        mJoinedRoomConfigLock.unlock();
        return answer;
    }

    public static void setmJoinedRoomConfig(RoomConfig mJoinedRoomConfig) {
        mJoinedRoomConfigLock.lock();
        NetworkData.mJoinedRoomConfig = mJoinedRoomConfig;
        mJoinedRoomConfigLock.lock();
    }

    public static String getmMyParticipantId() {
        mMyParticipantIdLock.lock();
        String answer = mMyParticipantId;
        mMyParticipantIdLock.unlock();
        return answer;
    }

    public static void setmMyParticipantId(String mMyParticipantId) {
        mMyParticipantIdLock.lock();
        NetworkData.mMyParticipantId = mMyParticipantId;
        mMyParticipantIdLock.unlock();
    }

    public static Room getmRoom() {
        mRoomLock.lock();
        Room answer = mRoom;
        mRoomLock.unlock();
        return answer;
    }

    public static void setmRoom(Room mRoom) {
        mRoomLock.lock();
        NetworkData.mRoom = mRoom;
        mRoomLock.unlock();
    }

    public static boolean ismPlaying() {
        mPlayingLock.lock();
        boolean answer = mPlaying;
        mPlayingLock.unlock();
        return answer;
    }

    public static void setmPlaying(boolean mPlaying) {
        mPlayingLock.lock();
        NetworkData.mPlaying = mPlaying;
        mPlayingLock.unlock();
    }
}
