package me.hwproj.mafiagame.startup;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import me.hwproj.mafiagame.gameflow.Player;
import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.gameflow.Settings;
import me.hwproj.mafiagame.impltest.network.ServerByteSender;
import me.hwproj.mafiagame.networking.FullGameState;
import me.hwproj.mafiagame.networking.MetaInformation;
import me.hwproj.mafiagame.networking.ServerSender;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phases.PlayerAction;

public class ServerGame {
    public static final byte ACTION_HEADER = 101;
    @NotNull
    private final Settings settings;
    private final ServerByteSender sender;
    private final Server server;

    public ServerGame(@NotNull Settings settings, ServerByteSender sender) {
        this.settings = settings;
        this.sender = sender;

        server = new Server(settings, new Sender());
    }

    public void receiveClientMessage(byte[] message) throws DeserializationException {
        if (message.length == 0) {
            Log.d("Bug", "receiveServerMessage: empty message received");
            return;
        }
        InputStream stream = new ByteArrayInputStream(message);

        try (DataInputStream dataStream = new DataInputStream(stream)) {
            int b = dataStream.read();

            switch (b) {
                case ACTION_HEADER:
                    receiveActionMessage(dataStream);
                    break;
                default: throw new DeserializationException("Unexpected package, code " + b);
            }

        } catch (IOException e) {
            throw new DeserializationException("Cant read first byte", e);
        }
    }

    private void receiveActionMessage(DataInputStream dataStream) throws DeserializationException {
        try {
            int phaseNum = dataStream.readInt();
            PlayerAction action = server.currentGameData.phases.get(phaseNum).deserialize(dataStream);
            server.acceptPlayerAction(action);
        } catch (IOException e) {
            throw new DeserializationException(e);
        }
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
