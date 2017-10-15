package com.github.games647.scoreboardstats.scoreboard;

import com.comphenix.net.sf.cglib.proxy.Factory;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.ScoreboardAction;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;

import static com.comphenix.protocol.PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE;
import static com.comphenix.protocol.PacketType.Play.Server.SCOREBOARD_OBJECTIVE;
import static com.comphenix.protocol.PacketType.Play.Server.SCOREBOARD_SCORE;
import static com.comphenix.protocol.PacketType.Play.Server.SCOREBOARD_TEAM;

/**
 * Listening all outgoing packets and check + handle for possibly client crash cases. This Listener should only read and
 * listen to relevant packets.
 * <p>
 * Protocol specifications can be found here http://wiki.vg/Protocol
 */
class PacketListener extends PacketAdapter {

    private final PacketManager manager;

    PacketListener(Plugin plugin, PacketManager manager) {
        super(plugin, SCOREBOARD_DISPLAY_OBJECTIVE, SCOREBOARD_OBJECTIVE, SCOREBOARD_SCORE, SCOREBOARD_TEAM);

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

        UUID playerUUID = player.getUniqueId();

        //handle async packets by other plugins
        if (Bukkit.isPrimaryThread()) {
            ensureMainThread(playerUUID, packet);
        } else {
            PacketContainer clone = packet.deepClone();
            Bukkit.getScheduler().runTask(plugin, () -> ensureMainThread(playerUUID, clone));
        }
    }

    private void ensureMainThread(UUID uuid, PacketContainer packet) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }

        PacketType packetType = packet.getType();
        if (packetType.equals(SCOREBOARD_SCORE)) {
            handleScorePacket(player, packet);
        } else if (packetType.equals(SCOREBOARD_OBJECTIVE)) {
            handleObjectivePacket(player, packet);
        } else if (packetType.equals(SCOREBOARD_DISPLAY_OBJECTIVE)) {
            handleDisplayPacket(player, packet);
        } else if (packetType.equals(SCOREBOARD_TEAM)) {
            handleTeamPacket(player, packet);
        }
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

    private void handleTeamPacket(Player player, PacketContainer packet) {
        String teamId = packet.getStrings().read(0);
        Optional<TeamMode> optMode = TeamMode.getMode(packet.getIntegers().read(0));

        if (!optMode.isPresent() || teamId.length() > 16) {
            return;
        }

        TeamMode mode = optMode.get();

        PlayerScoreboard scoreboard = manager.getScoreboard(player);
        if (mode == TeamMode.CREATE) {
            Collection<String> members = packet.getSpecificModifier(Collection.class).read(0);
            scoreboard.teamsByName.put(teamId, new Team(scoreboard, teamId, members));
        } else if (mode == TeamMode.REMOVE) {
            scoreboard.teamsByName.remove(teamId);
        } else if (mode == TeamMode.ADD_MEMBER) {
            Collection<String> members = packet.getSpecificModifier(Collection.class).read(0);
            scoreboard.getTeam(teamId).ifPresent(team -> team.members.addAll(members));
        } else if (mode == TeamMode.REMOVE_MEMBER) {
            Collection<String> members = packet.getSpecificModifier(Collection.class).read(0);
            scoreboard.getTeam(teamId).ifPresent(team -> team.members.removeAll(members));
        }
    }
}
