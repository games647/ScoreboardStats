package com.github.games647.scoreboardstats.scoreboard;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.github.games647.scoreboardstats.ScoreboardStats;
import com.google.common.collect.Maps;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Represents the scoreboard overview in a server-side perspective.
 */
public class PlayerScoreboard {

    private final Player player;

    final Map<String, Objective> objectivesByName = Maps.newHashMap();
    Objective sidebarObjective;

    public PlayerScoreboard(Player player) {
        this.player = player;
    }

    public Objective getOrCreateObjective(String objectiveId) {
        Objective objective = objectivesByName.get(objectiveId);
        if (objective == null) {
            objective = addObjective(objectiveId);
        }

        return objective;
    }

    public Optional<Objective> getObjective(String objectiveId) {
        return Optional.ofNullable(objectivesByName.get(objectiveId));
    }

    public Objective addObjective(String objectiveId) {
        return addObjective(objectiveId, objectiveId);
    }

    public Objective addObjective(String objectiveId, String display) {
        Objective objective = new Objective(this, objectiveId, display);
        sidebarObjective = objective;
        objectivesByName.put(objectiveId, objective);

        objective.sendObjectivePacket(State.CREATE);
        objective.sendShowPacket();
        return objective;
    }

    public Collection<Objective> getObjectives() {
        return objectivesByName.values();
    }

    public Optional<Objective> getSidebarObjective() {
        return Optional.ofNullable(sidebarObjective);
    }

    public void removeObjective(String objectiveId) {
        Objective objective = objectivesByName.remove(objectiveId);
        objective.sendObjectivePacket(State.REMOVE);
    }

    public Player getOwner() {
        return player;
    }

    void sendPacket(PacketContainer packet) {
        //add metadata that we ignore our packets on the listener
        packet.addMetadata("ScoreboardStats", true);

        try {
            //false so we don't listen to our own packets
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (InvocationTargetException ex) {
            //just log it for now.
            JavaPlugin.getPlugin(ScoreboardStats.class).getLog().info("Failed to send packet", ex);
        }
    }
}
