package me.hwproj.mafiagame.impltest.network;

import android.util.Log;

import androidx.core.util.Consumer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import me.hwproj.mafiagame.networking.ClientSender;
import me.hwproj.mafiagame.networking.ServerNetworkPackage;
import me.hwproj.mafiagame.phases.PlayerAction;
import me.hwproj.mafiagame.startup.ClientGame;
import me.hwproj.mafiagame.startup.ServerGame;
import me.hwproj.mafiagame.util.MyConsumer;

public class NetworkSimulator implements ClientSender, ServerByteSender {

    private final BlockingQueue<PlayerAction> actionQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<byte[]> serverMessages = new LinkedBlockingQueue<>();

    private Consumer<PlayerAction> actionConsumer;
    private MyConsumer<byte[]> clientConsumer;

    @Override
    public void sendPlayerAction(PlayerAction action) {
        try {
            actionQueue.put(action);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("Bad", "Interrupt while sending");
        }
    }



    @Override
    public void sendToEveryone(byte[] message) {
        serverMessages.add(message);
    }

    public void start(ClientGame client, ServerGame server) {
        this.actionConsumer = server::getAction;
        this.clientConsumer = client::receiveBytes;

        Thread consumeAction = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    actionConsumer.accept(actionQueue.take());
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
