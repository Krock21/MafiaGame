package me.hwproj.mafiagame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import me.hwproj.mafiagame.content.phases.doctor.DoctorPhase;
import me.hwproj.mafiagame.content.phases.mafia.MafiaPhase;
import me.hwproj.mafiagame.content.phases.vote.VotePhase;
import me.hwproj.mafiagame.gameflow.PlayerSettings;
import me.hwproj.mafiagame.gameflow.Settings;
import me.hwproj.mafiagame.gameplay.Role;
import me.hwproj.mafiagame.impltest.TestPhase;
import me.hwproj.mafiagame.impltest.network.NetworkSimulator;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phases.GamePhase;
import me.hwproj.mafiagame.startup.ClientGame;
import me.hwproj.mafiagame.startup.InitGamePackage;
import me.hwproj.mafiagame.startup.ServerGame;

public class PhaseActivity extends AppCompatActivity {

    private ClientGame game;

    private void startTestClient() {
        NetworkSimulator net = new NetworkSimulator();

        List<GamePhase> phases = Arrays.asList(new TestPhase(), new VotePhase(), new DoctorPhase(), new MafiaPhase());
        List<PlayerSettings> playerSettings = Arrays.asList(
                new PlayerSettings(Role.CITIZEN, "Pathfinder"),
                new PlayerSettings(Role.DOCTOR, "Lifeline"),
                new PlayerSettings(Role.MAFIA, "Caustic")
        );
        Settings settings = new Settings(phases, playerSettings);

        ServerGame serverGame = new ServerGame(net);

//        client = new Client(net, settings, 1, this::dealWithGameState);
        game = new ClientGame(net, this, this::transactionProvider, "You");

        ByteArrayOutputStream outs = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(outs);
        try {
            dataStream.write(ClientGame.INIT_PACKAGE_HEADER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            new InitGamePackage(settings, 1).serialize(dataStream);
        } catch (SerializationException e) {
//            e.printStackTrace();
            throw new RuntimeException(e);
        }
        byte[] message = outs.toByteArray();
        Log.d("ser", "startTestClient: serialized to " + Arrays.toString(message));
        try {
            game.receiveServerMessage(message);
        } catch (DeserializationException e) {
            Log.d("Bug", "startTestClient: deserialize exception");
//            e.printStackTrace();
            throw new RuntimeException(e);
        }

        net.start(game, serverGame);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phase);

        startTestClient();

//        Button b = findViewById(R.id.testid); // TODO delete
//        b.setOnClickListener(v -> client.startNextPhase(thisPhaseNumber + 1));
    }








    public FragmentTransaction transactionProvider() {
        return getSupportFragmentManager().beginTransaction();
    }

    private int thisPhaseNumber;
}

