package me.hwproj.mafiagame.startup;

import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Supplier;
import androidx.fragment.app.FragmentTransaction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import me.hwproj.mafiagame.ClientByteSender;
import me.hwproj.mafiagame.R;
import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.networking.ClientSender;
import me.hwproj.mafiagame.networking.FullGameState;
import me.hwproj.mafiagame.networking.MetaInformation;
import me.hwproj.mafiagame.networking.ServerNetworkPackage;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phases.GameState;
import me.hwproj.mafiagame.phases.PhaseFragment;
import me.hwproj.mafiagame.phases.PlayerAction;

public class ClientGame {
    public static final byte INIT_PACKAGE_HEADER = 3;
    public static final byte GAME_STATE_HEADER = 4;
    public static final byte META_HEADER = 5;

    private final ClientByteSender sender;
    private final AppCompatActivity activityReference;
    private Supplier<FragmentTransaction> transactionSupplier;
    private Client client;
    private boolean initialised;

    public Client getClient() { // TODO delete
        return client;
    } // TODO delete

    private PhaseFragment currentPhaseFragment;
    private int thisPhaseNumber = -1;

    public ClientGame(ClientByteSender sender, AppCompatActivity activityReference, Supplier<FragmentTransaction> transactionSupplier) {
        this.sender = sender;
        this.activityReference = activityReference;
        this.transactionSupplier = transactionSupplier;

        sendInitRequest();
    }

    public void receiveServerMessage(byte[] message) throws DeserializationException {
        if (message.length == 0) {
            Log.d("Bug", "receiveServerMessage: empty message received");
            return;
        }
        InputStream stream = new ByteArrayInputStream(message);

        try (DataInputStream dataStream = new DataInputStream(stream)) {
            int b = dataStream.read();

            switch (b) {
                case INIT_PACKAGE_HEADER:
                    initialise(InitGamePackage.deserialize(dataStream));
                    break;
                case GAME_STATE_HEADER:
                    receiveGameState(dataStream);
                    break;
                case META_HEADER:
                    receiveMeta(dataStream);
                    break;
                default: throw new DeserializationException("Unexpected package, code " + b);
            }

        } catch (IOException e) {
            throw new DeserializationException("Cant read first byte", e);
        }
    }


    private void sendInitRequest() {
        byte[] m = { ServerGame.INIT_REQUEST_HEADER };
        sender.sendBytesToServer(m);
    }

    private void receiveMeta(DataInputStream data) {
        MetaInformation meta;
        try {
            meta = MetaInformation.deserialize(data);
        } catch (DeserializationException e) {
            Log.d("Bug", "receiveMeta: deserialize error");
            e.printStackTrace();
            return;
        }
        receivePackage(new ServerNetworkPackage(meta));
    }

    private void receiveGameState(DataInputStream dataStream) {
        if (!initialised) {
            Log.d("Bug", "receiveGameState: uninitialized client");
            return;
        }
        FullGameState state;
        try {
            state = FullGameState.deserialize(dataStream, client.getGameData().phases);
        } catch (DeserializationException e) {
            Log.d("Bug", "receiveGameState: deserialization exception");
            e.printStackTrace();
            return;
        }
        receivePackage(new ServerNetworkPackage(state));
    }

    private void initialise(InitGamePackage init) {
        if (initialised) {
            Log.d("Bug", "initialise: double initialization");
            return;
        }
        client = new Client(new SenderConverter(), init.getGameSettings(), init.getPlayerNumber(), this::dealWithGameState);
        initialised = true;
        client.getPhaseNumberData().observe(activityReference, this::dealWithPhaseNumber);
    }

    private void receivePackage(ServerNetworkPackage pack) {
        Log.d("Net", "receivePackage: received, init = " + initialised);
        if (initialised) {
            client.receivePackage(pack);
        }
    }

    private void startPhaseFragment(PhaseFragment fg) {
        if (currentPhaseFragment != null) {
            transactionSupplier.get().remove(currentPhaseFragment).commit();
        }
        currentPhaseFragment = fg;

//        GameState state = client.getLatestGameState().getValue();
//        if (state != null) {
//            dealWithGameState(state);
//        }

        transactionSupplier.get().add(R.id.fragmentLayout, fg).commit();

        Button b = activityReference.findViewById(R.id.testid); // TODO delete
        b.setText(fg.getClass().getName());
    }

    // handler
    private void dealWithGameState(GameState state) {
        if (currentPhaseFragment != null) {
            if (currentPhaseFragment.isSubscribedToGameState()) {
                currentPhaseFragment.processGameState(state);
            }
        }
    }

    // handler
    private void dealWithPhaseNumber(Integer number) {
        if (number < thisPhaseNumber) {
            Log.d("Bad", "Wrong phase number:" + thisPhaseNumber + " -> " + number);
        }

        if (number > thisPhaseNumber) {
            Log.d("Ok", "transition to next phase:" + thisPhaseNumber + " -> " + number);
            if (currentPhaseFragment != null) {
                currentPhaseFragment.onPhaseEnd();
            }
            startPhaseFragment(client.nextPhaseFragment());
            thisPhaseNumber = number;
        }
    }

    private class SenderConverter implements ClientSender {

        @Override
        public void sendPlayerAction(PlayerAction action) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(bout);
            try {
                dout.writeByte(ServerGame.ACTION_HEADER);
                dout.writeInt(thisPhaseNumber % client.getGameData().phases.size());
                client.getGameData().phases.get(thisPhaseNumber).serializeAction(dout, action);
            } catch (SerializationException | IOException e) {
                Log.d("Bug", "sendPlayerAction: error while serializing");
                e.printStackTrace();
                return;
            }
            sender.sendBytesToServer(bout.toByteArray());

        }
    }
}
