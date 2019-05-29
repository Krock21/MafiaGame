package me.hwproj.mafiagame.gameplay;

import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;

public enum Role {
    CITIZEN(true),
    MAFIA(false),
    DOCTOR(true),
    INVESTIGATOR(true);

    private final boolean good;

    Role(boolean good) {
        this.good = good;
    }

    public boolean isGood() {
        return good;
    }

    public static Role deserialize(byte num) throws DeserializationException {
        switch (num) {
            case 1: return CITIZEN;
            case 2: return MAFIA;
            case 3: return DOCTOR;
            case 4: return INVESTIGATOR;
            default: throw new DeserializationException("Unrecognized role");
        }
    }

    public static byte serialize(Role role) throws SerializationException {
        switch (role) {
            case CITIZEN: return 1;
            case MAFIA: return 2;
            case DOCTOR: return 3;
            case INVESTIGATOR: return 4;
            default: throw new SerializationException("Unrecognized role");
        }
    }
}
