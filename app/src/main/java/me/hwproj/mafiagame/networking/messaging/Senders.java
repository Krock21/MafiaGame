package me.hwproj.mafiagame.networking.messaging;

import android.util.Log;

import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.tasks.Task;

import me.hwproj.mafiagame.gameinterface.GameActivity;
import me.hwproj.mafiagame.menu.MainActivity;
import me.hwproj.mafiagame.networking.NetworkData;

public class Senders {
    private GameActivity activity;
    private final NetworkData networkData;

    public Senders(GameActivity activity, NetworkData networkData) {
        this.activity = activity;
        this.networkData = networkData;
    }

    private void sendBytesToParticipant(String participantId, byte[] message, int sendsCount) {
        if (sendsCount <= 0) {
            Log.e(MainActivity.TAG, "sendBytes with sendsCount <= 0 to " + participantId);
            return;
        }
        if (!participantId.equals(networkData.getmMyParticipantId())) {
            Task<Integer> task = networkData.getRealTimeMultiplayerClient()
                    .sendReliableMessage(message, networkData.getmRoom().getRoomId(), participantId,
                            new RealTimeMultiplayerClient.ReliableMessageSentCallback() {
                                @Override
                                public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientId) {
                                    // handle the message being sent.
                                    if (statusCode != GamesCallbackStatusCodes.OK) {
                                        Log.d(MainActivity.TAG, "Message Lost to " + recipientId + Integer.toString(sendsCount));
                                        sendBytesToParticipant(participantId, message, sendsCount - 1);
                                    }
                                }
                            }
                    );
        } else {
            activity.messageReceived(networkData.getmMyParticipantId(), message);
        }
    }

    public static byte[] addToBegin(byte[] message, byte firstByte) {
        byte[] newMessage = new byte[message.length + 1];
        newMessage[0] = firstByte;
        for (int i = 0; i < message.length; i++) {
            newMessage[i + 1] = message[i];
        }
        return newMessage;
    }

    public static byte[] removeFromBegin(byte[] message) {
        byte[] newMessage = new byte[message.length - 1];
        for (int i = 0; i < message.length - 1; i++) {
            newMessage[i] = message[i + 1];
        }
        return newMessage;
    }

    public ServerByteSender serverSender = new ServerByteSender() {
        @Override
        public void broadcastMessage(byte[] message) {
            for (String participantId : networkData.getmRoom().getParticipantIds()) {
                sendMessage(participantId, message);
            }
        }

        @Override
        public void sendMessage(String participantId, byte[] message) {
            message = addToBegin(message, (byte) 0); // to client
            sendBytesToParticipant(participantId, message, 100);
        }
    };


    public ClientByteSender clientSender = new ClientByteSender() {
        @Override
        public void sendBytesToServer(byte[] message) {
            message = addToBegin(message, (byte) 1); // to server
            for (String participantId : networkData.getmRoom().getParticipantIds()) {
                sendBytesToParticipant(participantId, message, 100);
            }
        }
    };

}
