package me.hwproj.mafiagame.startup;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.gameflow.Settings;
import me.hwproj.mafiagame.impltest.network.ServerByteSender;
import me.hwproj.mafiagame.networking.FullGameState;
import me.hwproj.mafiagame.networking.MetaInformation;
import me.hwproj.mafiagame.networking.ServerSender;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phases.PlayerAction;

public class ServerGame {
    @NotNull
    private final Settings settings;
    private final ServerByteSender sender;
    private final Server server;

    public ServerGame(@NotNull Settings settings, ServerByteSender sender) {
        this.settings = settings;
        this.sender = sender;

        server = new Server(settings, new Sender());
    }

    public void getAction(PlayerAction a) {
        server.acceptPlayerAction(a);
    }

    public void receiveClientMessage(byte[] message) {
        // TODO
    }

    private class Sender implements ServerSender {

        @Override
        public void sendGameState(FullGameState state) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(bout);
            try {
                dout.writeByte(ClientGame.GAME_STATE_HEADER);
                state.serialize(dout, server.currentGameData.phases);
            } catch (SerializationException | IOException e) {
                Log.d("Net", "sendGameState: error while serializing");
                e.printStackTrace();
                return;
            }
            sender.sendToEveryone(bout.toByteArray());
        }

        @Override
        public void sendMetaInformation(MetaInformation info) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(bout);
            try {
                dout.writeByte(ClientGame.META_HEADER);
                info.serialize(dout);
            } catch (SerializationException | IOException e) {
                Log.d("Net", "sendMeta: error while serializing");
                e.printStackTrace();
                return;
            }
            sender.sendToEveryone(bout.toByteArray());
        }
    }
}
