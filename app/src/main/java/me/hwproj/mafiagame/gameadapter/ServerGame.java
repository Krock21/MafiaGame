package me.hwproj.mafiagame.gameadapter;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import me.hwproj.mafiagame.menu.MainActivity;
import me.hwproj.mafiagame.content.phases.infoPhase.InfoPhase;
import me.hwproj.mafiagame.content.phases.wait.WaitPhase;
import me.hwproj.mafiagame.gameflow.PlayerSettings;
import me.hwproj.mafiagame.networking.messaging.ServerByteSender;
import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.gameflow.Settings;
import me.hwproj.mafiagame.networking.FullGameState;
import me.hwproj.mafiagame.networking.MetaInformation;
import me.hwproj.mafiagame.networking.ServerSender;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phase.GamePhase;
import me.hwproj.mafiagame.phase.PlayerAction;

public class ServerGame {
    public static final byte ACTION_HEADER = 101;
    public static final byte INIT_REQUEST_HEADER = 102;

    private Settings settings;
    private final ServerByteSender sender;
    private final HashMap<String,String> idToName;
    private Server server;
    private boolean initialized;

    public ServerGame(ServerByteSender sender) {
        this.sender = sender;
        initialized = false;
        idToName = new HashMap<>();
    }

    /**
     * Tries to initialize server.
     */
    public void initialize(@NotNull Settings settings) {
        List<GamePhase> actualPhases = new ArrayList<>();
        actualPhases.add(new InfoPhase());
        for (GamePhase phase : settings.phases) {
            actualPhases.add(phase);
            actualPhases.add(new WaitPhase());
        }
        settings.phases = actualPhases;
        this.settings = settings;

        Collections.shuffle(settings.playerSettings);
        if (settings.playerSettings.size() != idToName.size()) {
            Log.d(MainActivity.TAG, "initialize: " + settings.playerSettings.size() + "players, but " + idToName.size() + "clients");
        }
        ListIterator<PlayerSettings> iter = settings.playerSettings.listIterator();
        for (Map.Entry<String, String> idName : idToName.entrySet()) {
            if (!iter.hasNext()) {
                break;
            }
            PlayerSettings s = iter.next();
            s.name = idName.getValue();
        }

        server = new Server(settings, new Sender());
        initialized = true;

        int playerNumber = 0;
        for (Map.Entry<String, String> idName : idToName.entrySet()) {
            initClient(idName.getKey(), playerNumber);
            Log.d(MainActivity.TAG, "initialize: " + idName.getKey() + " " + idName.getValue());
            playerNumber++;
        }

        server.initialize();

    }

    public void receiveClientMessage(byte[] message, String id) throws DeserializationException {
        if (message.length == 0) {
            Log.d("Bug", "receiveServerMessage: empty message received");
            return;
        }
        InputStream stream = new ByteArrayInputStream(message);

        try (DataInputStream dataStream = new DataInputStream(stream)) {
            int b = dataStream.readByte();

            switch (b) {
                case ACTION_HEADER:
                    receiveActionMessage(dataStream);
                    break;
                case INIT_REQUEST_HEADER:
                    rememberClient(id, dataStream);
                    break;
                default: throw new DeserializationException("Unexpected package, code " + b);
            }

        } catch (IOException e) {
            throw new DeserializationException("Cant read first byte", e);
        }
    }

    private void rememberClient(String id, DataInputStream dataStream) throws DeserializationException {
        try {
            String name = dataStream.readUTF();
            Log.d(MainActivity.TAG, "rememberClient: " + name);
            idToName.put(id, name);
        } catch (IOException e) {
            throw new DeserializationException(e);
        }

        Log.d(MainActivity.TAG, "rememberClient: got " + idToName.size() + " players");
    }

    private void initClient(String id, int playerNumber) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try (DataOutputStream dataStream = new DataOutputStream(stream)) {
            dataStream.writeByte(ClientGame.INIT_PACKAGE_HEADER);
            new InitGamePackage(settings, playerNumber).serialize(dataStream);
            sender.sendMessage(id, stream.toByteArray());
        } catch (IOException | SerializationException e) {
            Log.d("Bug", "initClient: ...");
            e.printStackTrace();
        }
    }

    private void receiveActionMessage(DataInputStream dataStream) throws DeserializationException {
        if (!initialized) {
            Log.d("Bug", "receiveActionMessage before initializing server");
        }
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
            sender.broadcastMessage(bout.toByteArray());
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
            sender.broadcastMessage(bout.toByteArray());
        }
    }
}
