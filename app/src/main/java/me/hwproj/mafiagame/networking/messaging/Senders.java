package me.hwproj.mafiagame.networking.messaging;

import androidx.annotation.NonNull;

import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashSet;

import static me.hwproj.mafiagame.networking.NetworkData.getRealTimeMultiplayerClient;
import static me.hwproj.mafiagame.networking.NetworkData.getmRoom;

public class Senders {
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
            message = addToBegin(message, (byte) 0);
            for (String participantId : getmRoom().getParticipantIds()) {
                Task<Integer> task = getRealTimeMultiplayerClient()
                        .sendReliableMessage(message, getmRoom().getRoomId(), participantId,
                                handleMessageSentCallback).addOnCompleteListener(new OnCompleteListener<Integer>() {
                            @Override
                            public void onComplete(@NonNull Task<Integer> task) {
                                // Keep track of which messages are sent, if desired.
                                recordMessageToken(task.getResult());
                            }
                        });
            }
        }

        @Override
        public void sendMessage(String participantId, byte[] message) {
            message = addToBegin(message, (byte) 0);
            Task<Integer> task = getRealTimeMultiplayerClient()
                    .sendReliableMessage(message, getmRoom().getRoomId(), participantId,
                            handleMessageSentCallback).addOnCompleteListener(new OnCompleteListener<Integer>() {
                        @Override
                        public void onComplete(@NonNull Task<Integer> task) {
                            // Keep track of which messages are sent, if desired.
                            recordMessageToken(task.getResult());
                        }
                    });
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

        HashSet<Integer> pendingMessageSet = new HashSet<>();

        synchronized void recordMessageToken(int tokenId) {
            pendingMessageSet.add(tokenId);
        }
    };


    public ClientByteSender clientSender = new ClientByteSender() {
        @Override
        public void sendBytesToServer(byte[] message) {
            message = addToBegin(message, (byte) 1);
            for (String participantId : getmRoom().getParticipantIds()) {
                Task<Integer> task = getRealTimeMultiplayerClient()
                        .sendReliableMessage(message, getmRoom().getRoomId(), participantId,
                                handleMessageSentCallback).addOnCompleteListener(new OnCompleteListener<Integer>() {
                            @Override
                            public void onComplete(@NonNull Task<Integer> task) {
                                // Keep track of which messages are sent, if desired.
                                recordMessageToken(task.getResult());
                            }
                        });
            }
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

        HashSet<Integer> pendingMessageSet = new HashSet<>();

        synchronized void recordMessageToken(int tokenId) {
            pendingMessageSet.add(tokenId);
        }
    };

}
