package me.hwproj.mafiagame.impltest.network;

import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.networking.ClientSender;
import me.hwproj.mafiagame.networking.FullGameState;
import me.hwproj.mafiagame.networking.MetaInformation;
import me.hwproj.mafiagame.networking.ServerNetworkPackage;
import me.hwproj.mafiagame.networking.ServerSender;
import me.hwproj.mafiagame.phases.PlayerAction;
import me.hwproj.mafiagame.util.MyConsumer;

public class NaiveNetworkSimulator implements ClientSender, ServerSender {

    private final BlockingQueue<PlayerAction> actionQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<ServerNetworkPackage> packQueue = new LinkedBlockingQueue<>();

    private MyConsumer<PlayerAction> actionConsumer;
    private MyConsumer<ServerNetworkPackage> clientConsumer;

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
    public void sendGameState(FullGameState state) {
        try {
            packQueue.put(new ServerNetworkPackage(state));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("Bad", "Interrupt while sending");
        }
    }

    @Override
    public void sendMetaInformation(MetaInformation info) {
        try {
            packQueue.put(new ServerNetworkPackage(info));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("Bad", "Interrupt while sending");
        }
    }

    public void start(Client client, Server server) {
        this.actionConsumer = server::acceptPlayerAction;
        this.clientConsumer = client::receivePackage;

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
                    ServerNetworkPackage taken = packQueue.take();
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

    public NaiveNetworkSimulator() { }
}
