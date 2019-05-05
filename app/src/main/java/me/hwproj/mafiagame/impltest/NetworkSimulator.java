package me.hwproj.mafiagame.impltest;

import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.networking.ClientSender;
import me.hwproj.mafiagame.networking.MetaCrouch;
import me.hwproj.mafiagame.networking.ServerSender;
import me.hwproj.mafiagame.phases.GameState;
import me.hwproj.mafiagame.phases.PlayerAction;
import me.hwproj.mafiagame.util.MyConsumer;

public class NetworkSimulator implements ClientSender, ServerSender {

    private final BlockingQueue<PlayerAction> actionQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<GameStateOrMeta> stateQueue = new LinkedBlockingQueue<>();

    private MyConsumer<PlayerAction> actionConsumer;
    private MyConsumer<GameState> stateConsumer;
    private MyConsumer<MetaCrouch> metaConsumer;

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
    public void sendGameState(GameState state) {
        try {
            stateQueue.put(new GameStateOrMeta(state));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("Bad", "Interrupt while sending");
        }
    }

    @Override
    public void sendMetaInformation(MetaCrouch info) {
        try {
            stateQueue.put(new GameStateOrMeta(info));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("Bad", "Interrupt while sending");
        }
    }

    public void start(Client client, Server server) {
        this.actionConsumer = server::acceptPlayerAction;
        this.stateConsumer = client::receiveGameState;
        this.metaConsumer = client::receiveMeta;

        Thread consumeAction = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    actionConsumer.accept(actionQueue.take());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    Log.d("Bad", "didnt work");
                }
            }
        });
        Thread consumeState = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    GameStateOrMeta taken = stateQueue.take();
                    if (taken.state != null) {
                        stateConsumer.accept(taken.state);
                    } else {
                        metaConsumer.accept(taken.meta);
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    Log.d("Bad", "didnt work");
                }
            }
        });
        consumeAction.start();
        consumeState.start();
    }

    public NetworkSimulator() {
    }

    private static class GameStateOrMeta {
        private GameState state;
        private MetaCrouch meta;

        public GameStateOrMeta(GameState state) {
            this.state = state;
        }

        public GameStateOrMeta(MetaCrouch meta) {
            this.meta = meta;
        }

        public GameState getState() {
            return state;
        }

        public MetaCrouch getMeta() {
            return meta;
        }
    }
}
