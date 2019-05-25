package me.hwproj.mafiagame.util;

public class ModifiableBoolean {
    public ModifiableBoolean(boolean value) {
        this.value = value;
    }

    private boolean value;

    public boolean get() {
        return value;
    }

    public void set(boolean value) {
        this.value = value;
    }
}
