package me.hwproj.mafiagame.networking;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import me.hwproj.mafiagame.gameflow.ServerGameData;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phase.GamePhaseClient;
import me.hwproj.mafiagame.phase.GamePhaseServer;
import me.hwproj.mafiagame.phase.GameState;

public class FullGameState {
    private int phaseNumber;
    private boolean[] isDead;
    private GameState phaseState;

    public FullGameState(ServerGameData data, GameState phaseState) {
        isDead = new boolean[data.players.size()];
        for (int i = 0; i < data.players.size(); i++) {
            isDead[i] = data.players.get(i).dead;
        }
        phaseNumber = data.phaseNumber;
        this.phaseState = phaseState;
    }

    public boolean[] getIsDead() {
        return isDead;
    }

    public GameState getPhaseState() {
        return phaseState;
    }

    public static FullGameState deserialize(DataInputStream data, List<GamePhaseClient> phases) throws DeserializationException {
        try {
            int phaseNumber = data.readInt();
            boolean[] isDead = new boolean[data.readInt()];
            for (int i = 0; i < isDead.length; i++) {
                isDead[i] = data.readByte() != 0;
            }

            GameState phaseState = phases.get(phaseNumber % phases.size()).deserializeGameState(data);

            return new FullGameState(phaseNumber, isDead, phaseState);
        } catch (IOException e) {
            throw new DeserializationException(e);
        }

    }

    public void serialize(DataOutputStream data, List<GamePhaseServer> phases) throws SerializationException {
        try {
            data.writeInt(phaseNumber);
            data.writeInt(isDead.length);
            for (int i = 0; i < isDead.length; i++) {
                byte dead = 0;
                if (isDead[i]) {
                    dead = 1;
                }
                data.writeByte(dead);
            }

            try {
                phases.get(phaseNumber % phases.size()).serializeGameState(data, phaseState);
            } catch (SerializationException e) {
                Log.d("Bug", "serialize state: phaseNumber = " + phaseNumber + ", phase state " + phaseState.getClass() + ", phase " + phases.get(phaseNumber % phases.size()).getClass());
                throw new SerializationException(e);
            }
        } catch (IOException e) {
            throw new SerializationException(e);
        }

    }

    private FullGameState(int phaseNumber, boolean[] isDead, GameState phaseState ) {
        this.phaseNumber = phaseNumber;
        this.isDead = isDead;
        this.phaseState = phaseState;
    }
}
