package me.hwproj.mafiagame.networking.serialization;

import androidx.core.util.Supplier;

import com.google.android.gms.games.Game;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

import me.hwproj.mafiagame.content.phases.doctor.DoctorPhase;
import me.hwproj.mafiagame.content.phases.mafia.MafiaPhase;
import me.hwproj.mafiagame.content.phases.vote.VotePhase;
import me.hwproj.mafiagame.impltest.TestPhase;
import me.hwproj.mafiagame.phases.GamePhase;

public class GamePhaseSerializer {
    private static final PhaseIdentifier[] phases = {
            new PhaseIdentifier(1, TestPhase::new, TestPhase.class),
            new PhaseIdentifier(2, VotePhase::new, VotePhase.class),
            new PhaseIdentifier(3, DoctorPhase::new, DoctorPhase.class),
            new PhaseIdentifier(4, MafiaPhase::new, MafiaPhase.class)
    };

    public static GamePhase deserialize(InputStream stream) throws DeserializationException {
        int b;
        try {
            b = stream.read();
        } catch (IOException e) {
            throw new DeserializationException("Cant read 1 byte", e);
        }
        if (b == -1) {
            throw new DeserializationException("No input left");
        }

        for (PhaseIdentifier s : phases) {
            if (b == s.phaseIdentifier) {
                return s.construct();
            }
        }
        throw new DeserializationException("Unrecognized phase number");
    }

    public static byte serialize(Class<? extends GamePhase> clazz) throws SerializationException {
        for (PhaseIdentifier s : phases) {
            int id = s.getPhaseIdentifier(clazz);
            if (id != 0) {
                return (byte) id;
            }
        }
        throw new SerializationException("Unrecognized phase");
    }

    private static class PhaseIdentifier {
        @NotNull
        private final Supplier<GamePhase> supplier;
        private final int phaseIdentifier;
        private Class<? extends GamePhase> phaseClass;

        public PhaseIdentifier(int phaseIdentifier,
                                @NotNull Supplier<GamePhase> supplier,
                                @NotNull Class<? extends GamePhase> phaseClass) {
            this.supplier = supplier;
            this.phaseIdentifier = phaseIdentifier;
            this.phaseClass = phaseClass;
        }

        public int getPhaseIdentifier(Class<? extends  GamePhase> clazz) {
            if (clazz == phaseClass) {
                return phaseIdentifier;
            }
            return 0;
        }

        @NotNull
        public GamePhase construct() {
            return supplier.get();
        }
    }
}