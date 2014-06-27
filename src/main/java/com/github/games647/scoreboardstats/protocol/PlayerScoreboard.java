package com.github.games647.scoreboardstats.protocol;

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

    private Objective curSidebarObjective;

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
     * @param force should it overwrite the sidebar objective This could end in conflicts if you overwrite it from other plugins
     * @return the created objective
     * @throws NullPointerException name is null
     * @throws NullPointerException displayName is null
     * @throws IllegalArgumentException name is longer than 16 characters
     * @throws IllegalArgumentException displayname is longer than 32 characters
     * @throws IllegalStateException if there is already a objective with that name
     * @throws IllegalStateException if there is already a sidebar objective active
     */
    public Objective createSidebarObjective(String objectiveName, String displayName, boolean force)
            throws NullPointerException, IllegalArgumentException, IllegalStateException {
        Preconditions.checkNotNull(objectiveName, "objective name cannot be null");
        Preconditions.checkNotNull(displayName, "display name cannot be null");

        Preconditions.checkArgument(objectiveName.length() <= 16, "objective name is longer than 16 characters");
        Preconditions.checkArgument(displayName.length() <= 32, "display name is longer than 32 characters");

        if (!force) {
            Preconditions.checkState(curSidebarObjective == null, "There is already an sidebar objective");
        }

        if (objectivesByName.containsKey(objectiveName)) {
            //the objecive already exits. I assume that no other use this unique name
            //so we expect that a other sidebar was showing
            final Objective objective = objectivesByName.get(objectiveName);
            PacketFactory.sendDisplayPacket(objective);
            curSidebarObjective = objective;
            return objective;
        }

        final Objective objective = new Objective(this, objectiveName, displayName);
        curSidebarObjective = objective;
        objectivesByName.put(objectiveName, objective);
        return objective;
    }

    /**
     * Gets the current sidebar objective
     *
     * @return current sidebar objective or null
     */
    public Objective getSidebarObjective() {
        return curSidebarObjective;
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
    public Player getPlayer() {
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
        if (curSidebarObjective != null && curSidebarObjective.getName().equals(objectiveName)) {
            clearSidebarObjective();
        }
    }

    void setSidebarObjective(String objectiveName) {
        if (objectiveName.isEmpty()) {
            clearSidebarObjective();
        } else {
            curSidebarObjective = objectivesByName.get(objectiveName);
        }
    }

    void clearSidebarObjective() {
        curSidebarObjective = null;
    }

    void resetScore(String scoreName) {
        /*
         * Very weird that minecraft always ignore the name of the parent objective and
         * will remove the score from the complete scoreboard
         */
        for (Objective entry : objectivesByName.values()) {
            entry.items.remove(scoreName);
        }
    }

    void createOrUpdateScore(String scoreName, String parent, int score) {
        final Objective objective = objectivesByName.get(parent);
        if (objective != null) {
            final Item item = new Item(objective, scoreName, score, false);
            //This automatically replace the old one
            objective.items.put(scoreName, item);
        }
    }
}
