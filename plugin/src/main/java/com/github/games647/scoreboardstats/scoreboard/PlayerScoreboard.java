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

/**
 * Represents the scoreboard overview in a server-side perspective.
 */
public class PlayerScoreboard {

    private final ScoreboardStats plugin;
    private final Player player;

    final Map<String, Objective> objectivesByName = Maps.newHashMap();
    final Map<String, Team> teamsByName = Maps.newHashMap();

    Objective sidebarObjective;

    public PlayerScoreboard(ScoreboardStats plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public Objective getOrCreateObjective(String objectiveId) {
        return objectivesByName.computeIfAbsent(objectiveId, this::addObjective);
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
        if (objective != null) {
            objective.sendObjectivePacket(State.REMOVE);
        }
    }

    public Optional<Team> getTeam(String teamId) {
        return Optional.ofNullable(teamsByName.get(teamId));
    }

    public Team addTeam(String teamId) {
        Team team = new Team(this, teamId);
        teamsByName.put(teamId, team);

        team.sendCreatePacket();
        return team;
    }

    public Collection<Team> getTeams() {
        return teamsByName.values();
    }

    public void removeTeam(String teamId) {
        Team team = teamsByName.remove(teamId);
        if (team != null) {
            team.sendRemovePacket();
        }
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
            plugin.getLog().info("Failed to send packet", ex);
        }
    }
}
