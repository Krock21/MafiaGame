package me.hwproj.mafiagame.phases;

public abstract class PlayerAction {
    /**
     * String to identify phases in network operations
     * TODO mb should use non-final field to simulate a final field in an abstract class
     */
    abstract public String getPhaseName();
}
