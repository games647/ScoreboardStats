package com.github.games647.scoreboardstats.scoreboard.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;

/**
 * Listening all outgoing packets and check + handle for possibly client crash cases. This Listener should only read and
 * listen to relevant packets.
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
    private static final PacketType TEAM_TYPE = PacketType.Play.Server.SCOREBOARD_TEAM;

    protected final PacketSbManager manager;

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
    public void onPacketSending(PacketEvent packetEvent) {
        if (packetEvent.isCancelled() || packetEvent.getPlayer().getName().contains("UNKNOWN[")) {
            return;
        }

        Player player = packetEvent.getPlayer();
        PacketContainer packet = packetEvent.getPacket();

        PacketType packetType = packetEvent.getPacketType();

        //everything was read from the packet, so we don't need to access it anymore
        //we could now run a sync thread to sychronize with async packets
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (packetType.equals(SCORE_TYPE)) {
                handleScorePacket(player, packet);
            } else if (packetType.equals(OBJECTIVE_TYPE)) {
                handleObjectivePacket(player, packet);
            } else if (packetType.equals(DISPLAY_TYPE)) {
                handleDisplayPacket(player, packet);
            } else if (packetType.equals(TEAM_TYPE)) {
                handleTeamPacket(player, packet);
            }
        });
    }

    private void handleScorePacket(Player player, PacketContainer packet) {
        String scoreName = packet.getStrings().read(0);
        String parent = packet.getStrings().read(1);
        int score = packet.getIntegers().read(0);

        //state id
        Integer stateId = packet.getIntegers().readSafely(1);

        State action;
        if (stateId == null) {
            //an enum is used instead of an integer
            action = State.fromId(packet.getScoreboardActions().read(0).ordinal());
        } else {
            //old system no enum -> 1.5-1.7
            action = State.fromId(stateId);
        }

        //Packet receiving validation
        if (action == State.CREATE && parent.length() > 16) {
            //Invalid packet
            return;
        }

        PlayerScoreboard scoreboard = manager.getScoreboard(player);
        //scores actually only have two state id, because these
        if (action == State.CREATE) {
            scoreboard.createOrUpdateScore(scoreName, parent, score);
        } else if (action == State.REMOVE) {
            scoreboard.resetScore(scoreName);
        }
    }

    private void handleObjectivePacket(Player player, PacketContainer packet) {
        String objectiveName = packet.getStrings().read(0);
        //Can be empty
        String displayName = packet.getStrings().read(1);
        State action = State.fromId(packet.getIntegers().read(0));

        //Packet receiving validation
        if (objectiveName.length() > 16 || displayName.length() > 32) {
            //Invalid packet
            return;
        }

        PlayerScoreboard scoreboard = manager.getScoreboard(player);
        Objective objective = scoreboard.getObjective(objectiveName);
        if (action == State.CREATE) {
            scoreboard.addObjective(objectiveName, displayName);
        } else if (objective != null) {
            //Could cause a NPE at the client if the objective wasn't found
            if (action == State.REMOVE) {
                scoreboard.removeObjective(objectiveName);
            } else if (action == State.UPDATE) {
                objective.setDisplayName(displayName, false);
            }
        }
    }

    private void handleDisplayPacket(Player player, PacketContainer packet) {
        //Can be empty; if so it would just clear the slot
        String objectiveName = packet.getStrings().read(0);
        DisplaySlot slot = SlotTransformer.fromId(packet.getIntegers().read(0));

        //Packet receiving validation
        if (slot == null || objectiveName.length() > 16) {
            return;
        }

        PlayerScoreboard scoreboard = manager.getScoreboard(player);
        if (slot == DisplaySlot.SIDEBAR) {
            scoreboard.setSidebarObjective(objectiveName);
        } else {
            Objective sidebarObjective = scoreboard.getSidebarObjective();
            if (sidebarObjective != null && sidebarObjective.getName().equals(objectiveName)) {
                scoreboard.clearSidebarObjective();
            }
        }
    }

    private void handleTeamPacket(Player player, PacketContainer packet) {
        String teamName = packet.getStrings().read(0);
//        String displayName = packet.getStrings().read(1);
        String prefix = packet.getStrings().readSafely(2);
        String suffix = packet.getStrings().readSafely(3);
//        NameTagVisibility getStrings().read(4);

        //color getIntegers().read(0);
        @SuppressWarnings("unchecked")
        Collection<String> changedPlayer = packet.getSpecificModifier(Collection.class).readSafely(0);
        int mode = packet.getIntegers().read(1);
        //pack options getIntegers().read(2);

        PlayerScoreboard scoreboard = manager.getScoreboard(player);

        switch (mode) {
            //create team
            case 0:
                scoreboard.createTeam(teamName, prefix, suffix, changedPlayer);
                break;
            //remove
            case 1:
                scoreboard.removeTeam(teamName);
                break;
            //update team info
            case 2:
                scoreboard.updateTeamInfo(teamName, prefix, suffix);
                break;
            case 3:
                //add player
                scoreboard.addPlayerTeam(teamName, changedPlayer);
            case 4:
                //remove player
                scoreboard.removePlayerTeam(teamName, changedPlayer);
            default:
                //ignore
                break;
        }
    }
}
