package com.github.games647.scoreboardstats.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Listening all outgoing packets and check + handle for possibly client crash cases.
 * This Listener should only read and listen to relevant packets.
 *
 * Protocol specifications can be found here http://wiki.vg/Protocol
*
 * @see PacketFactory
 * @see PacketAdapter
 */
public class PacketListener extends PacketAdapter {

    //Shorter access
    private static final PacketType DISPLAY_TYPE = PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE;
    private static final PacketType OBJECTIVE_TYPE = PacketType.Play.Server.SCOREBOARD_OBJECTIVE;
    private static final PacketType SCORE_TYPE = PacketType.Play.Server.SCOREBOARD_SCORE;

    private final PacketSbManager manager;

    /**
     * Creates a new packet listener
     *
     * @param plugin plugin for registration into ProtcolLib
     * @param manager packet manager instance
     */
    public PacketListener(Plugin plugin, PacketSbManager manager) {
        super(plugin, DISPLAY_TYPE, OBJECTIVE_TYPE, SCORE_TYPE);

        this.manager = manager;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final PacketType packetType = event.getPacketType();
        if (packetType.equals(SCORE_TYPE)) {
            handleScorePacket(event);
        } else if (packetType.equals(OBJECTIVE_TYPE)) {
            handleObjectivePacket(event);
        } else if (packetType.equals(DISPLAY_TYPE)) {
            handleDisplayPacket(event);
        }
    }

    private void handleScorePacket(PacketEvent event) {
        final Player player = event.getPlayer();
        final PacketContainer packet = event.getPacket();

        final String scoreName = packet.getStrings().read(0);
        final String parent = packet.getStrings().read(1);
        final int score = packet.getIntegers().read(0);

        //state id
        final StructureModifier<Enum> enumModifier = packet.getSpecificModifier(Enum.class);
        final Enum<?> scoreboardActions = enumModifier.readSafely(0);

        State action;
        if (scoreboardActions == null) {
            //old system
            action = State.fromId(packet.getIntegers().read(1));
        } else {
            action = State.fromId(scoreboardActions.ordinal());
        }

        //Packet receiving validation
        if (scoreName.length() > 16
                || action == State.CREATE && parent.length() > 16) {
            //Invalid packet
            return;
        }

        final PlayerScoreboard scoreboard = manager.getScoreboard(player);
        //scores actually only have two state id, because these
        if (action == State.CREATE) {
            scoreboard.createOrUpdateScore(scoreName, parent, score);
        } else if (action == State.REMOVE) {
            scoreboard.resetScore(scoreName);
        }
    }

    private void handleObjectivePacket(PacketEvent event) {
        final Player player = event.getPlayer();
        final PacketContainer packet = event.getPacket();

        final String objectiveName = packet.getStrings().read(0);
        //Can be empty
        final String displayName = packet.getStrings().read(1);
        final State action = State.fromId(packet.getIntegers().read(0));

        //Packet receiving validation
        if (objectiveName.length() > 16 || displayName.length() > 32) {
            //Invalid packet
            return;
        }

        final PlayerScoreboard scoreboard = manager.getScoreboard(player);
        final Objective objective = scoreboard.getObjective(objectiveName);
        if (action == State.CREATE) {
            scoreboard.addObjective(objectiveName, displayName);
        } else if (objective != null) {
            //Could cause a NPE at the client if the objective wasn't found
            if (action == State.REMOVE) {
                scoreboard.removeObjective(objectiveName);
            } else if (action == State.UPDATE_TITLE) {
                objective.setDisplayName(displayName, false);
            }
        }
    }

    private void handleDisplayPacket(PacketEvent event) {
        final Player player = event.getPlayer();
        final PacketContainer packet = event.getPacket();

        //Can be empty; if so it would just clear the slot
        final String objectiveName = packet.getStrings().read(0);
        final Slot slot = Slot.fromId(packet.getIntegers().read(0));

        //Packet receiving validation
        if (slot == null || objectiveName.length() > 16) {
            return;
        }

        final PlayerScoreboard scoreboard = manager.getScoreboard(player);
        if (slot == Slot.SIDEBAR) {
            scoreboard.setSidebarObjective(objectiveName);
        } else {
            final Objective sidebarObjective = scoreboard.getSidebarObjective();
            if (sidebarObjective != null && sidebarObjective.getName().equals(objectiveName)) {
                scoreboard.clearSidebarObjective();
            }
        }
    }
}
