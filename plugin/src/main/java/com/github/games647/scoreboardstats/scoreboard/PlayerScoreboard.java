package com.github.games647.scoreboardstats.scoreboard;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

import org.bukkit.entity.Player;

/**
 * Represents the scoreboard overview in a server-side perspective.
 */
public class PlayerScoreboard {

    private final Player player;

    private final Map<String, Objective> objectivesByName = Maps.newHashMap();
    private final Map<String, Team> teamByName = Maps.newHashMap();

    private Objective sidebarObjective;

    /**
     * Creates a new scoreboard for specific player.
     *
     * @param player the player for the scoreboard
     */
    public PlayerScoreboard(Player player) {
        this.player = player;
    }

    /**
     * Creates a new sidebar objective.
     *
     * @param objectiveName the objective name. have to be unique
     * @param displayName the displayed name
     * @param replace overwrite the sidebar slot. Conflicts if you overwrite scoreboards from other plugins
     * @return the created objective
     * @throws NullPointerException name is null
     * @throws NullPointerException displayName is null
     * @throws IllegalArgumentException name is longer than 16 characters
     * @throws IllegalArgumentException display-name is longer than 32 characters
     * @throws IllegalStateException if there is already a objective with that name
     * @throws IllegalStateException if there is already a sidebar objective active
     */
    public Objective createSidebarObjective(String objectiveName, String displayName, boolean replace)
            throws NullPointerException, IllegalArgumentException, IllegalStateException {
        Preconditions.checkNotNull(objectiveName, "objective name cannot be null");
        Preconditions.checkNotNull(displayName, "display name cannot be null");

        Preconditions.checkArgument(objectiveName.length() <= 16, "objective name is longer than 16 characters");
        Preconditions.checkArgument(displayName.length() <= 32, "display name is longer than 32 characters");

        if (!replace) {
            Preconditions.checkState(sidebarObjective == null, "There is already an sidebar objective");
        }

        if (objectivesByName.containsKey(objectiveName)) {
            //the objective already exits. I assume that no other use this unique name
            //so we expect that a other sidebar was showing
            Objective objective = objectivesByName.get(objectiveName);
            PacketFactory.sendDisplayPacket(objective);
            sidebarObjective = objective;
            return objective;
        }

        Objective objective = new Objective(this, objectiveName, displayName);
        sidebarObjective = objective;
        objectivesByName.put(objectiveName, objective);
        return objective;
    }

    /**
     * Gets the current sidebar objective
     *
     * @return current sidebar objective or null
     */
    public Objective getSidebarObjective() {
        return sidebarObjective;
    }

    /**
     * Gets all objectives that the player have as immutable collection.
     *
     * @return the objectives
     */
    public Collection<Objective> getObjectives() {
        return ImmutableSet.copyOf(objectivesByName.values());
    }

    /**
     * Gets the owner of this scoreboard
     *
     * @return the owner of the scoreboard
     */
    public Player getOwner() {
        return player;
    }

    void addObjective(String objectiveName, String displayName) {
        objectivesByName.put(objectiveName, new Objective(this, objectiveName, displayName, false));
    }

    Objective getObjective(String name) {
        return objectivesByName.get(name);
    }

    void removeObjective(String objectiveName) {
        objectivesByName.remove(objectiveName);
        if (sidebarObjective != null && sidebarObjective.getName().equals(objectiveName)) {
            clearSidebarObjective();
        }
    }

    void setSidebarObjective(String objectiveName) {
        if (objectiveName.isEmpty()) {
            clearSidebarObjective();
        } else {
            sidebarObjective = objectivesByName.get(objectiveName);
        }
    }

    void clearSidebarObjective() {
        sidebarObjective = null;
    }

    void resetScore(String scoreName) {
        /*
         * Very weird that minecraft always ignore the name of the parent objective and
         * will remove the score from the complete scoreboard
         */
        objectivesByName.values().forEach(entry -> entry.items.remove(scoreName));
    }

    void createOrUpdateScore(String scoreName, String parent, int score) {
        Objective objective = objectivesByName.get(parent);
        if (objective != null) {
            Item item = new Item(objective, scoreName, score, false);
            //This automatically replace the old one
            objective.items.put(scoreName, item);
        }
    }

    void createTeam(String teamName, String prefix, String suffix, Collection<String> changedPlayer) {
        Team team = new Team(teamName, prefix, suffix);
        teamByName.put(teamName, team);
    }

    void removeTeam(String teamName) {
        teamByName.remove(teamName);
    }

    void updateTeamInfo(String teamName, String prefix, String suffix) {
        Team team = teamByName.get(teamName);
        if (team != null) {
            team.setPrefix(prefix);
            team.setSuffix(suffix);
        }
    }

    void addPlayerTeam(String teamName, Iterable<String> changedPlayer) {
        Team team = teamByName.get(teamName);
        if (team != null) {
            changedPlayer.forEach(team::addEntry);
        }
    }

    void removePlayerTeam(String teamName, Iterable<String> changedPlayer) {
        Team team = teamByName.get(teamName);
        if (team != null) {
            changedPlayer.forEach(team::removeEntry);
        }
    }
}
