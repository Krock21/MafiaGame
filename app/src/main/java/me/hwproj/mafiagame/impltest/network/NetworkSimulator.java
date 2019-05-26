package me.hwproj.mafiagame.impltest.network;

import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import me.hwproj.mafiagame.ClientByteSender;
import me.hwproj.mafiagame.ServerByteSender;
import me.hwproj.mafiagame.startup.ClientGame;
import me.hwproj.mafiagame.startup.ServerGame;
import me.hwproj.mafiagame.util.MyConsumer;

public class NetworkSimulator implements ClientByteSender, ServerByteSender {

    private final BlockingQueue<byte[]> clientMessages = new LinkedBlockingQueue<>();
    private final BlockingQueue<byte[]> serverMessages = new LinkedBlockingQueue<>();

    private MyConsumer<byte[]> actionConsumer;
    private MyConsumer<byte[]> clientConsumer;

    @Override
    public void sendBytesToServer(byte[] message) {
        clientMessages.add(message);
    }

    @Override
    public void broadcastMessage(byte[] message) {
        serverMessages.add(message);
    }

    @Override
    public void sendMessage(String participantId, byte[] message) {
        // TODO or not to do
    }

    public void start(ClientGame client, ServerGame server) {
        this.actionConsumer = server::receiveClientMessage;
        this.clientConsumer = client::receiveServerMessage;

        Thread consumeAction = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    actionConsumer.accept(clientMessages.take());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    Log.d("Bad", "consume action thread throws");
                }
            }
        });
        Thread consumeState = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    byte[] taken = serverMessages.take();
                    clientConsumer.accept(taken);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    Log.d("Bad", "consume state throws");
                }
            }
        });
        consumeAction.start();
        consumeState.start();
    }

    public NetworkSimulator() { }
}
