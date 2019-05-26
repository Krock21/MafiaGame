package me.hwproj.mafiagame.gameplay;

import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.SerializationException;

public enum Role {
    CITIZEN,
    MAFIA,
    DOCTOR;

    public static Role deserialize(byte num) throws DeserializationException {
        switch (num) {
            case 1: return CITIZEN;
            case 2: return MAFIA;
            case 3: return DOCTOR;
            default: throw new DeserializationException("Unrecognized role");
        }
    }

    public static byte serialize(Role role) throws SerializationException {
        switch (role) {
            case CITIZEN: return 1;
            case MAFIA: return 2;
            case DOCTOR: return 3;
            default: throw new SerializationException("Unrecognized role");
        }
    }
}
