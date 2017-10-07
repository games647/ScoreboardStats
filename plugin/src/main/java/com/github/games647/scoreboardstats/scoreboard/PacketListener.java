package com.github.games647.scoreboardstats.scoreboard;

import com.comphenix.net.sf.cglib.proxy.Factory;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.ScoreboardAction;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;

import static com.comphenix.protocol.PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE;
import static com.comphenix.protocol.PacketType.Play.Server.SCOREBOARD_OBJECTIVE;
import static com.comphenix.protocol.PacketType.Play.Server.SCOREBOARD_SCORE;

/**
 * Listening all outgoing packets and check + handle for possibly client crash cases. This Listener should only read and
 * listen to relevant packets.
 * <p>
 * Protocol specifications can be found here http://wiki.vg/Protocol
 */
class PacketListener extends PacketAdapter {

    private final PacketManager manager;

    PacketListener(Plugin plugin, PacketManager manager) {
        super(plugin, SCOREBOARD_DISPLAY_OBJECTIVE, SCOREBOARD_OBJECTIVE, SCOREBOARD_SCORE);

        this.manager = manager;
    }

    @Override
    public void onPacketSending(PacketEvent packetEvent) {
        Player player = packetEvent.getPlayer();
        if (packetEvent.isCancelled() || player instanceof Factory) {
            return;
        }

        PacketContainer packet = packetEvent.getPacket();
        if (packet.hasMetadata("ScoreboardStats")) {
            //it's our own packet
            return;
        }

        PacketType packetType = packetEvent.getPacketType();

        //everything was read from the packet, so we don't need to access it anymore
        //we could now run a sync thread to synchronize with async packets
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (packetType.equals(SCOREBOARD_SCORE)) {
                handleScorePacket(player, packet);
            } else if (packetType.equals(SCOREBOARD_OBJECTIVE)) {
                handleObjectivePacket(player, packet);
            } else if (packetType.equals(SCOREBOARD_DISPLAY_OBJECTIVE)) {
                handleDisplayPacket(player, packet);
            }
        });
    }

    private void handleScorePacket(Player player, PacketContainer packet) {
        String scoreName = packet.getStrings().read(0);
        String parent = packet.getStrings().read(1);
        int score = packet.getIntegers().read(0);

        //state id
        ScoreboardAction action = packet.getScoreboardActions().read(0);

        //Packet receiving validation
        if (parent.length() > 16) {
            //Invalid packet
            return;
        }

        PlayerScoreboard scoreboard = manager.getScoreboard(player);
        //scores actually only have two state id, because these
        if (action == ScoreboardAction.CHANGE) {
            scoreboard.getObjective(parent).ifPresent(objective -> objective.scores.put(scoreName, score));
        } else if (action == ScoreboardAction.REMOVE) {
            scoreboard.getObjective(parent).ifPresent(objective -> objective.scores.remove(scoreName, score));
        }
    }

    private void handleObjectivePacket(Player player, PacketContainer packet) {
        String objectiveId = packet.getStrings().read(0);
        //Can be empty
        String displayName = packet.getStrings().read(1);
        State action = State.fromId(packet.getIntegers().read(0));

        //Packet receiving validation
        if (objectiveId.length() > 16 || displayName.length() > 32) {
            //Invalid packet
            return;
        }

        PlayerScoreboard scoreboard = manager.getScoreboard(player);
        if (action == State.CREATE) {
            Objective objective = new Objective(scoreboard, objectiveId, displayName);
            scoreboard.objectivesByName.put(objectiveId, objective);
        } else {
            if (action == State.REMOVE) {
                scoreboard.objectivesByName.remove(objectiveId);
            } else {
                scoreboard.getObjective(objectiveId).ifPresent(obj -> obj.displayName = displayName);
            }
        }
    }

    private void handleDisplayPacket(Player player, PacketContainer packet) {
        //Can be empty; if so it would just clear the slot
        String objectiveId = packet.getStrings().read(0);
        Optional<DisplaySlot> slot = SlotTransformer.fromId(packet.getIntegers().read(0));

        //Packet receiving validation
        if (!slot.isPresent() || objectiveId.length() > 16) {
            return;
        }

        PlayerScoreboard scoreboard = manager.getScoreboard(player);
        if (slot.get() == DisplaySlot.SIDEBAR) {
            scoreboard.getObjective(objectiveId).ifPresent(obj -> scoreboard.sidebarObjective = obj);
        } else {
            scoreboard.getSidebarObjective().filter(objective -> objective.getId().equals(objectiveId))
                    .ifPresent(objective -> scoreboard.sidebarObjective = null);
        }
    }
}
