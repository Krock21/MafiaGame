package me.hwproj.mafiagame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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
    private static final int RC_SIGN_IN = 9000;
    private GoogleSignInAccount googleSignInAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TestStringHolder h = ViewModelProviders.of(this).get(TestStringHolder.class);
        Button bAddC = findViewById(R.id.button);
        Button bReset = findViewById(R.id.button2);
        Button bSignIn = findViewById(R.id.signin);
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

        bSignIn.setOnClickListener(v -> {
            startSignInIntent();
        });

        //signInSilently();
    }

    private void signInSilently() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(MainActivity.this.getResources().getString(R.string.server_client_id))
                .build();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
            // Already signed in.
            // The signed in account is stored in the 'account' variable.
            googleSignInAccount = account;
        } else {
            // Haven't been signed-in before. Try the silent sign-in first.
            GoogleSignInClient signInClient = GoogleSignIn.getClient(this, signInOptions);
            signInClient
                    .silentSignIn()
                    .addOnCompleteListener(
                            this,
                            new OnCompleteListener<GoogleSignInAccount>() {
                                @Override
                                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                                    if (task.isSuccessful()) {
                                        // The signed in account is stored in the task's result.
                                        googleSignInAccount = task.getResult();
                                    } else {
                                        // Player will need to sign-in explicitly using via UI.
                                        // See [sign-in best practices](http://developers.google.com/games/services/checklist) for guidance on how and when to implement Interactive Sign-in,
                                        // and [Performing Interactive Sign-in](http://developers.google.com/games/services/android/signin#performing_interactive_sign-in) for details on how to implement
                                        // Interactive Sign-in.
                                        // signing in
                                        startSignInIntent();
                                    }
                                }
                            });
        }
    }

    private void startSignInIntent() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(MainActivity.this.getResources().getString(R.string.server_client_id))
                .build();
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                signInOptions);
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                googleSignInAccount = result.getSignInAccount();
                new AlertDialog.Builder(this).setMessage("SigningIn SUCCESS")
                        .setNeutralButton(android.R.string.ok, null).show();
            } else {
                String message = result.getStatus().toString();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        }
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
