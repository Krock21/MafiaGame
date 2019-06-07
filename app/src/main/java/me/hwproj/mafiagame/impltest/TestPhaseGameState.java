package me.hwproj.mafiagame.impltest;

import android.os.Parcel;

import me.hwproj.mafiagame.phase.GameState;

public class TestPhaseGameState extends GameState {
    private int sum;

    public TestPhaseGameState(int sum) {
        this.sum = sum;
    }

    public int getSum() {
        return sum;
    }

    private TestPhaseGameState(Parcel in) {
        sum = in.readInt();
    }

}
