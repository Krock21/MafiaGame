package me.hwproj.mafiagame;

import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import me.hwproj.mafiagame.content.phases.doctor.DoctorPhase;
import me.hwproj.mafiagame.content.phases.mafia.MafiaPhase;
import me.hwproj.mafiagame.content.phases.vote.VotePhase;
import me.hwproj.mafiagame.gameflow.PlayerSettings;
import me.hwproj.mafiagame.gameflow.Settings;
import me.hwproj.mafiagame.gameplay.Role;
import me.hwproj.mafiagame.impltest.TestPhase;
import me.hwproj.mafiagame.networking.serialization.DeserializationException;
import me.hwproj.mafiagame.networking.serialization.GamePhaseSerializer;
import me.hwproj.mafiagame.networking.serialization.SerializationException;
import me.hwproj.mafiagame.phases.GamePhase;

import static org.junit.jupiter.api.Assertions.*;

public class serializationTest {

    private ByteArrayOutputStream byteStream;

    private DataOutputStream getOutput() {
        byteStream = new ByteArrayOutputStream();
        return new DataOutputStream(byteStream);
    }

    private DataInputStream getInput() {
        return new DataInputStream(new ByteArrayInputStream(byteStream.toByteArray()));
    }

    @Test
    public void rolesSerialization() throws SerializationException, DeserializationException {
        Role[] roles = {Role.MAFIA, Role.CITIZEN, Role.DOCTOR};

        for (Role r : roles) {
            byte b = Role.serialize(r);
            assertEquals(r, Role.deserialize(b));
        }
    }

    @Test
    public void phaseSerialization() throws SerializationException, IOException, DeserializationException {
        GamePhase[] phases = { new TestPhase(), new VotePhase(), new MafiaPhase(), new DoctorPhase() };

        for (GamePhase phase : phases) {
            DataOutputStream dout = getOutput();
            byte b = GamePhaseSerializer.serialize(phase.getClass());
            dout.write(b);
            assertEquals(phase.getClass(), GamePhaseSerializer.deserialize(getInput()).getClass());
        }
    }

    @Test
    public void playerSettingSerialization() throws SerializationException, IOException, DeserializationException {
        PlayerSettings example = new PlayerSettings(Role.DOCTOR, "Bob");

        DataOutputStream dout = getOutput();
        example.serialize(dout);
        PlayerSettings deserialized = PlayerSettings.deserialize(getInput());
        assertEquals(example.role, deserialized.role);
        assertEquals(example.name, deserialized.name);
    }


    @Test
    public void settingSerialization() throws SerializationException, IOException, DeserializationException {
        List<PlayerSettings> players = Arrays.asList(
                new PlayerSettings(Role.DOCTOR, "Vlad molodec"),
                new PlayerSettings(Role.MAFIA, "Gaev petuh"),
                new PlayerSettings(Role.CITIZEN, "Kek")
        );
        List<GamePhase> phases = Arrays.asList(
                new TestPhase(),
                new VotePhase(),
                new MafiaPhase(),
                new DoctorPhase()
        );

        Settings s = new Settings(phases, players);

        DataOutputStream dout = getOutput();
        s.serialize(dout);
        Settings deserialized = Settings.deserialize(getInput());

        assertEquals(s.phases.size(), deserialized.phases.size());
        assertEquals(s.playerSettings.size(), deserialized.playerSettings.size());
        for (int i = 0; i < s.phases.size(); i++) {
            assertEquals(s.phases.get(i).getClass(), deserialized.phases.get(i).getClass());
        }

        for (int i = 0; i < s.playerSettings.size(); i++) {
            assertEquals(s.playerSettings.get(i).role, deserialized.playerSettings.get(i).role);
            assertEquals(s.playerSettings.get(i).name, deserialized.playerSettings.get(i).name);
        }
    }
}
