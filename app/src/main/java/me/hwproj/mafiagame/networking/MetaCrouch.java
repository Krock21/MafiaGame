package me.hwproj.mafiagame.networking;

public class MetaCrouch {
    public static final int NEXT_PHASE = 1;

    private int whatInt;
    private int phaseNum;

    public static MetaCrouch nextPhase(int num) {
        MetaCrouch m = new MetaCrouch();
        m.whatInt = NEXT_PHASE;
        m.phaseNum = num;
        return m;
    }

    public int what() {
        return whatInt;
    }

    public int getNumber() {
        return phaseNum;
    }

    private MetaCrouch() {}
}
