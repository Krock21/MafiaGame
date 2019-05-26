package me.hwproj.mafiagame.startup;

import android.util.Log;
import android.widget.Button;

import androidx.core.util.Consumer;
import androidx.core.util.Supplier;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import me.hwproj.mafiagame.PhaseActivity;
import me.hwproj.mafiagame.R;
import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.networking.ClientSender;
import me.hwproj.mafiagame.networking.ServerNetworkPackage;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.phases.GameState;
import me.hwproj.mafiagame.phases.PhaseFragment;

public class ClientGame {
    public static final byte INIT_PACKAGE_HEADER = 3;
    private final ClientSender sender;
    private final PhaseActivity activityReference;
    private Supplier<FragmentTransaction> transactionSupplier;
    private Client client;
    private boolean initialised;

    public Client getClient() { // TODO delete
        return client;
    }

    private PhaseFragment currentPhaseFragment;
    private int thisPhaseNumber = -1;

    public ClientGame(ClientSender sender, PhaseActivity activityReference, Supplier<FragmentTransaction> transactionSupplier) {
        this.sender = sender;
        this.activityReference = activityReference;
        this.transactionSupplier = transactionSupplier;
    }
    public void receiveBytes(byte[] message) throws DeserializationException {
        if (message.length == 0) {
            Log.d("Bug", "receiveBytes: empty message received");
            return;
        }
        InputStream stream = new ByteArrayInputStream(message);

        try (DataInputStream dataStream = new DataInputStream(stream)) {
            int b = dataStream.read();

            switch (b) {
                case INIT_PACKAGE_HEADER: initialise(InitGamePackage.deserialize(dataStream));
                    break;
                default: throw new DeserializationException("Unexpected package, code " + b);
            }

        } catch (IOException e) {
            throw new DeserializationException("Cant read first byte", e);
        }
    }

    private void initialise(InitGamePackage init) {
        client = new Client(sender, init.getGameSettings(), init.getPlayerNumber(), this::dealWithGameState);
        initialised = true;
        client.getPhaseNumberData().observe(activityReference, this::dealWithPhaseNumber);
    }

    private void receivePackage(ServerNetworkPackage pack) {
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
}
