package me.hwproj.mafiagame.networking;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NetworkData {
    public static final int RC_GAMES_SIGN_IN = 9001;
    public static final int RC_SELECT_PLAYERS = 9006;
    public static final int RC_WAITING_ROOM = 9007;
    public static final int RC_INVITATION_INBOX = 9008;
    private final Lock mJoinedRoomConfigLock = new ReentrantLock();
    private RoomConfig mJoinedRoomConfig;
    private final Lock mMyParticipantIdLock = new ReentrantLock();
    private String mMyParticipantId;
    private final Lock mRoomLock = new ReentrantLock();
    private Room mRoom;
    private static final Lock googleSignInAccountLock = new ReentrantLock();
    private static GoogleSignInAccount googleSignInAccount;
    private final Lock realTimeMultiplayerClientLock = new ReentrantLock();
    private RealTimeMultiplayerClient realTimeMultiplayerClient;

    // are we already playing?
    private final Lock mPlayingLock = new ReentrantLock();
    private boolean mPlaying = false;

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

    public RealTimeMultiplayerClient getRealTimeMultiplayerClient() {
        realTimeMultiplayerClientLock.lock();
        RealTimeMultiplayerClient answer = realTimeMultiplayerClient;
        realTimeMultiplayerClientLock.unlock();
        return answer;
    }

    public void setRealTimeMultiplayerClient(RealTimeMultiplayerClient realTimeMultiplayerClient) {
        realTimeMultiplayerClientLock.lock();
        this.realTimeMultiplayerClient = realTimeMultiplayerClient;
        realTimeMultiplayerClientLock.unlock();
    }

    public RoomConfig getmJoinedRoomConfig() {
        mJoinedRoomConfigLock.lock();
        RoomConfig answer = mJoinedRoomConfig;
        mJoinedRoomConfigLock.unlock();
        return answer;
    }

    public void setmJoinedRoomConfig(RoomConfig mJoinedRoomConfig) {
        mJoinedRoomConfigLock.lock();
        this.mJoinedRoomConfig = mJoinedRoomConfig;
        mJoinedRoomConfigLock.lock();
    }

    public String getmMyParticipantId() {
        mMyParticipantIdLock.lock();
        String answer = mMyParticipantId;
        mMyParticipantIdLock.unlock();
        return answer;
    }

    public void setmMyParticipantId(String mMyParticipantId) {
        mMyParticipantIdLock.lock();
        this.mMyParticipantId = mMyParticipantId;
        mMyParticipantIdLock.unlock();
    }

    public Room getmRoom() {
        mRoomLock.lock();
        Room answer = mRoom;
        mRoomLock.unlock();
        return answer;
    }

    public void setmRoom(Room mRoom) {
        mRoomLock.lock();
        this.mRoom = mRoom;
        mRoomLock.unlock();
    }

    public boolean ismPlaying() {
        mPlayingLock.lock();
        boolean answer = mPlaying;
        mPlayingLock.unlock();
        return answer;
    }

}
