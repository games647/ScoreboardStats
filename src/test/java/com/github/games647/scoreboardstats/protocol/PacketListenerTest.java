package com.github.games647.scoreboardstats.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.github.games647.scoreboardstats.ScoreboardStats;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class PacketListenerTest {

    @BeforeClass
    public static void setUpClass() {
        //Ensure that package names are correctly set up.
        MinecraftReflection.setMinecraftPackage("net.minecraft.server.v1_7_R3", "org.bukkit.craftbukkit.v1_7_R3");
    }

    /**
     * Test of onPacketSending method, of class PacketListener.
     */
    @Test
    public void testOnPacketSending() {
        final ScoreboardStats plugin = Mockito.mock(ScoreboardStats.class);
        final PacketSbManager sbManager = Mockito.mock(PacketSbManager.class);
        Mockito.when(sbManager.getScoreboard(null)).thenReturn(new PlayerScoreboard(null));

        final PacketListener listener = new PacketListener(plugin, sbManager);

        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
        packet.getStrings().write(0, "123456789_1234567890_12345678789");
        PacketEvent packetEvent = PacketEvent.fromServer(new Object(), packet, null);
        listener.onPacketSending(packetEvent);

        //The above should fail silently
        Mockito.verifyZeroInteractions(sbManager);

        packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
        packetEvent = PacketEvent.fromServer(new Object(), packet, null);
        listener.onPacketSending(packetEvent);

        packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
        packet.getStrings().writeDefaults(); //Prevents NPEs
        packetEvent = PacketEvent.fromServer(new Object(), packet, null);
        listener.onPacketSending(packetEvent);

        packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        packet.getStrings().writeDefaults();
        packetEvent = PacketEvent.fromServer(new Object(), packet, null);
        listener.onPacketSending(packetEvent);

        Mockito.verify(sbManager, Mockito.atLeastOnce());
    }
}
