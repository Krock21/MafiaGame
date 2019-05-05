package me.hwproj.mafiagame.impltest;

import me.hwproj.mafiagame.phases.PlayerAction;

public class TestPhasePlayerAction extends PlayerAction {

    private int added;
    private boolean next;

    public static TestPhasePlayerAction nextPhase() {
        TestPhasePlayerAction n = new TestPhasePlayerAction();
        n.next = true;
        return n;
    }

    @Override
    public String getPhaseName() {
        return TestPhase.PHASE_NAME;
    }

    public TestPhasePlayerAction(int x) {
        added = x;
    }

    public int getAdded() {
        return added;
    }

    private TestPhasePlayerAction() {
    }

    public boolean isNext() {
        return next;
    }
}
