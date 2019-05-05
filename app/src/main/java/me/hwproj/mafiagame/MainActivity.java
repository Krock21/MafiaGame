package me.hwproj.mafiagame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.gameflow.Server;
import me.hwproj.mafiagame.gameflow.ServerGameData;
import me.hwproj.mafiagame.gameflow.Settings;
import me.hwproj.mafiagame.impltest.NetworkSimulator;
import me.hwproj.mafiagame.impltest.TestPhase;
import me.hwproj.mafiagame.impltest.TestPhaseActivity;
import me.hwproj.mafiagame.impltest.TestPhaseGameState;
import me.hwproj.mafiagame.impltest.TestPhaseServer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TestStringHolder h = ViewModelProviders.of(this).get(TestStringHolder.class);
        Button bAddC = findViewById(R.id.button);
        Button bReset = findViewById(R.id.button2);
        TextView text = findViewById(R.id.textView);

        bAddC.setOnClickListener(v -> h.append('c'));
        bReset.setOnClickListener(v -> h.setText(""));



        Thread threadPrinter = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
                h.append('t');
            }
        });
        threadPrinter.start();
        h.getData().observe(this, text::setText);

        Button start = findViewById(R.id.startTest);
        start.setOnClickListener(this::startTestPhase);
    }

    private void startTestPhase(View v) {
        NetworkSimulator net = new NetworkSimulator();

        Settings settings = new Settings();
        settings.phases = Arrays.asList(new TestPhase(), new TestPhase());

        Client.ConstructClient(net, settings);
        Client cl = Client.getClient(this);
        Server serv = new Server(settings, new ServerGameData(), net);

        net.start(cl, serv);

        startActivity(cl.nextPhaseActivity(this));
    }
}
