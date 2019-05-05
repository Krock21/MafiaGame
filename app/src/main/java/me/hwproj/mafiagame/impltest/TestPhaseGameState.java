package me.hwproj.mafiagame.impltest;

import android.os.Parcel;

import me.hwproj.mafiagame.phases.GameState;

public class TestPhaseGameState extends GameState {
    private int sum;

    public TestPhaseGameState(int sum) {
        this.sum = sum;
    }

    public int getSum() {
        return sum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sum);
    }

    private TestPhaseGameState(Parcel in) {
        sum = in.readInt();
    }

    public static final Creator CREATOR = new Creator<TestPhaseGameState>() {
        @Override
        public TestPhaseGameState createFromParcel(Parcel source) {
            return new TestPhaseGameState(source);
        }

        @Override
        public TestPhaseGameState[] newArray(int size) {
            return new TestPhaseGameState[size];
        }
    };
}
