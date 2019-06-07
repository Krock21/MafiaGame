package me.hwproj.mafiagame.gameadapter;

import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Supplier;
import androidx.fragment.app.FragmentTransaction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import me.hwproj.mafiagame.menu.MainActivity;
import me.hwproj.mafiagame.content.phases.wait.WaitClient;
import me.hwproj.mafiagame.gameflow.ClientCallbacks;
import me.hwproj.mafiagame.networking.messaging.ClientByteSender;
import me.hwproj.mafiagame.R;
import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.networking.ClientSender;
import me.hwproj.mafiagame.networking.FullGameState;
import me.hwproj.mafiagame.networking.MetaInformation;
import me.hwproj.mafiagame.networking.ServerNetworkPackage;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phase.GameState;
import me.hwproj.mafiagame.phase.PhaseFragment;
import me.hwproj.mafiagame.phase.PlayerAction;
import me.hwproj.mafiagame.util.Alerter;
import me.hwproj.mafiagame.util.NotifierInterractor;

public class ClientGame {
    public static final byte INIT_PACKAGE_HEADER = 3;
    public static final byte GAME_STATE_HEADER = 4;
    public static final byte META_HEADER = 5;

    private final ClientByteSender sender;
    private final AppCompatActivity activityReference; // TODO replace with callbacks
    private Supplier<FragmentTransaction> transactionSupplier;
    private String desiredName;
    private final Runnable onClientEndCallback;
    private Client client;
    private boolean initialised;

    private PhaseFragment currentPhaseFragment;
    private int thisPhaseNumber = -1;

    // TODO give it callbacks
    public ClientGame(ClientByteSender sender, AppCompatActivity activityReference, Supplier<FragmentTransaction> transactionSupplier, String desiredName, Runnable onClientEnd) {
        this.sender = sender;
        this.activityReference = activityReference;
        this.transactionSupplier = transactionSupplier;

//        sendInitRequest();
        // because on server device need to initialize callbacks before requesting initialization
        this.desiredName = desiredName;
        onClientEndCallback = onClientEnd;
    }

    public void receiveServerMessage(byte[] message) throws DeserializationException {
        if (message.length == 0) {
            Log.d("Bug", "receiveServerMessage: empty message received");
            return;
        }
        Log.d(MainActivity.TAG, "Client received message from server");
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


    public void sendInitRequest() {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        DataOutputStream ds = new DataOutputStream(bs);
        try {
            ds.writeByte(ServerGame.INIT_REQUEST_HEADER);
            ds.writeUTF(desiredName);

            sender.sendBytesToServer(bs.toByteArray());
        } catch (IOException e) {
            Log.d("Bug", "sendInitRequest: io error");
            e.printStackTrace();
        }
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
        client = new Client(new SenderConverter(), init.getGameSettings(), init.getPlayerNumber(), new ClientCallbacks() {
            @Override
            public void handleGameState(GameState state) {
                dealWithGameState(state);
            }

            @Override
            public void finishGame(String message) {
                Alerter.alert(activityReference, "Game finished", message);
                onClientEndCallback.run();
            }

            @Override
            public void setToolbarText(String text) {
                ClientGame.this.setToolbarText(text);
            }
        });
        initialised = true;

        client.getPhaseNumberData().observe(activityReference, this::dealWithPhaseNumber);
    }

    private void receivePackage(ServerNetworkPackage pack) {
        Log.d("Net", "receivePackage: received, client.init = " + initialised);
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
    }

    // handler
    private void dealWithGameState(GameState state) {
        Log.d("qwe", "dealWithGameState: receied state " + state);
        if (currentPhaseFragment != null) {
            Log.d("qwe", "dealWithGameState: fragment is not null " + state);
            if (currentPhaseFragment.isSubscribedToGameState()) {
                Log.d("qwe", "dealWithGameState: handing to fragment " + state);
                currentPhaseFragment.processGameState(state);
            }
        } else {
            Log.d("Bug", "dealWithGameState: received game state, but phase has not started yet");
        }
    }

    // handler
    private void dealWithPhaseNumber(Integer number) {
        if (number < thisPhaseNumber) {
            Log.d("Bad", "Wrong phase number:" + thisPhaseNumber + " -> " + number);
        }

        while (number > thisPhaseNumber) {
            Log.d("Ok", "transition to next phase:" + thisPhaseNumber + " -> " + number);
            if (currentPhaseFragment != null) {
                currentPhaseFragment.onPhaseEnd();
            }
            if (client.thisPlayer().dead) {
                onClientKilled();
                return;
            }

            startPhaseFragment(client.nextPhaseFragment());

            // TODO make vibration a callback for Client and remove this nonsense
            if (client.getGameData().currentPhase.getClass() != WaitClient.class) {
                NotifierInterractor.vibrate(activityReference.getApplicationContext(), 200);
                NotifierInterractor.playClick(activityReference.findViewById(R.id.fragmentLayout));
            }
            thisPhaseNumber++;
        }
    }

    private void onClientKilled() {
        client.onThisPlayerKilled();
        Alerter.alert(activityReference, "You died", "Any last words?");
        onClientEndCallback.run();
    }

    private void setToolbarText(String text) {
        Toolbar toolbar = activityReference.findViewById(R.id.toolbar);
        TextView toolbarText = toolbar.findViewById(R.id.toolbarTextView);
        toolbarText.setText(text);
    }

    private class SenderConverter implements ClientSender {

        @Override
        public void sendPlayerAction(PlayerAction action) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(bout);
            try {
                dout.writeByte(ServerGame.ACTION_HEADER);
                int phaseIndex = thisPhaseNumber % client.getGameData().phases.size();
                dout.writeInt(phaseIndex);
                client.getGameData().phases.get(phaseIndex).serializeAction(dout, action);
            } catch (SerializationException | IOException e) {
                Log.d("Bug", "sendPlayerAction: error while serializing");
                e.printStackTrace();
                return;
            }
            sender.sendBytesToServer(bout.toByteArray());

        }
    }
}
