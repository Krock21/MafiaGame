package me.hwproj.mafiagame.impltest;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import me.hwproj.mafiagame.gameflow.Client;
import me.hwproj.mafiagame.phases.GamePhaseClient;
import me.hwproj.mafiagame.phases.GameState;

public class TestPhaseClient implements GamePhaseClient {

    @Override
    public Class<? extends AppCompatActivity> createActivity() {
        return TestPhaseActivity.class;
    }

    @Override
    public void processGameState(GameState state) {
//        if (!(state instanceof TestPhaseGameState)) {
//            return;
//        }
//        TestPhaseGameState castedState = (TestPhaseGameState) state;
//
////        Intent intent = new Intent(Client.SEND_GAME_STATE_EVENT);
////        intent.putExtra(Client.GAME_STATE, castedState);
    }
}
