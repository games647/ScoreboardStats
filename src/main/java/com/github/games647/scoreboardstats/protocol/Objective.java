package com.github.games647.scoreboardstats.protocol;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.bukkit.entity.Player;

/**
 * Represents a sidebar objective
 *
 * @see Item
 */
@EqualsAndHashCode(doNotUseGetters = true)
@ToString(doNotUseGetters = true)
public final class Objective {

    //A scoreboard can't have more than 15 items
    final Map<String, Item> items = Maps.newHashMapWithExpectedSize(15);

    private final PlayerScoreboard scoreboard;

    private final String objectiveName;
    private String displayName;

    Objective(PlayerScoreboard scoreboard, String objectiveName, String displayName) {
        this(scoreboard, objectiveName, displayName, true);
    }

    Objective(PlayerScoreboard scoreboard, String objectiveName, String displayName, boolean send) {
        this.scoreboard = scoreboard;

        this.objectiveName = objectiveName;
        this.displayName = displayName;

        if (send) {
            PacketFactory.sendPacket(this, State.CREATED);
        }
    }

    /**
     * Check whether this item is shown to the player.
     *
     * @return whether this item is shown to the player
     */
    public boolean isShown() {
        //Prevents NPE with this ordering
        return this.equals(scoreboard.getSidebarObjective());
    }

    /**
     * Get the unique name for this objective.
     *
     * @return the unique name for this objective
     */
    public String getName() {
        return objectiveName;
    }

    /**
     * Get the displayed name.
     *
     * @return the displayed name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Set the display name
     *
     * @param displayName the new displayName
     * @throws NullPointerException if displayName is null
     * @throws IllegalArgumentException if displayName is longer than 32 characters
     * @throws IllegalStateException if this objective was removed
     */
    public void setDisplayName(String displayName)
            throws NullPointerException, IllegalArgumentException, IllegalStateException {
        setDisplayName(displayName, true);
    }

    /**
     * Set the display name
     *
     * @param displayName the new displayName
     * @param send should the displayname instantly be sended
     * @throws NullPointerException if displayName is null
     * @throws IllegalArgumentException if displayName is longer than 32 characters
     * @throws IllegalStateException if this objective was removed
     */
    public void setDisplayName(String displayName, boolean send)
            throws NullPointerException, IllegalArgumentException, IllegalStateException {
        Preconditions.checkState(isShown());

        Preconditions.checkNotNull(displayName);
        Preconditions.checkArgument(displayName.length() <= 32);

        if (!this.displayName.equals(displayName)) {
            this.displayName = displayName;
            if (send) {
                PacketFactory.sendPacket(this, State.UPDATE_TITLE);
            }
        }
    }

    /**
     * Add a new item/score to this objective.
     *
     * @param name new scoreboard item
     * @return the created item
     * @throws NullPointerException if name is null
     * @throws IllegalArgumentException if name is longer than 16 characters
     * @throws IllegalStateException if this objective was removed
     * @throws IllegalStateException if this objective already has 15 scoreboard items
     */
    public Item registerItem(String name)
            throws NullPointerException, IllegalArgumentException, IllegalStateException {
        return registerItem(name, 0);
    }

    /**
     * Add a new item/score with a predefined score to this objective.
     *
     * @param name the new scoreboard item
     * @param score the value for this scoreboard item
     * @return the created item
     * @throws NullPointerException if name is null
     * @throws IllegalArgumentException if name is longer than 16 characters
     * @throws IllegalStateException if this objective was removed
     * @throws IllegalStateException if this objective already has 15 scoreboard items
     */
    public Item registerItem(String name, int score)
            throws NullPointerException, IllegalArgumentException, IllegalStateException {
        return registerItem(name, score, true);
    }

    /**
     * Add a new item/score with a predefined score to this objective.
     *
     * @param name the new scoreboard item
     * @param score the value for this scoreboard item
     * @param send should be the item send instantly
     * @return the created item
     * @throws NullPointerException if name is null
     * @throws IllegalArgumentException if name is longer than 16 characters
     * @throws IllegalStateException if this objective was removed
     * @throws IllegalStateException if this objective already has 15 scoreboard items
     */
    public Item registerItem(String name, int score, boolean send)
            throws NullPointerException, IllegalArgumentException, IllegalStateException {
        Preconditions.checkState(isShown());

        Preconditions.checkNotNull(name);
        Preconditions.checkArgument(name.length() <= 16);

        Preconditions.checkState(items.size() <= 15);

        final Item scoreItem = new Item(this, name, score, send);
        items.put(name, scoreItem);

        return scoreItem;
    }

    /**
     * Gets the item/score with that name.
     *
     * @param name the name of the item
     * @return the item or null if no item item with that name exists
     */
    public Item getItem(String name) {
        return items.get(name);
    }

    /**
     * Gets a collection of all registered item/scores in this objective as an
     * immutable soted list.
     *
     * @return all items for this objective
     */
    public List<Item> getItems() {
        final List<Item> values = Lists.newArrayList(items.values());
        Collections.sort(values);
        return ImmutableList.copyOf(values);
    }

    /**
     * Removes an item/score from this objective.
     *
     * @param name name of the score
     * @throws NullPointerException if name is null
     * @throws IllegalArgumentException if name is longer than 16 characters
     * @throws IllegalStateException if this objective was removed
     */
    public void unregisterItem(String name)
            throws NullPointerException, IllegalArgumentException, IllegalStateException {
        Preconditions.checkState(isShown());

        Preconditions.checkNotNull(name);
        Preconditions.checkArgument(name.length() <= 16);

        final Item item = items.remove(name);
        if (item != null) {
            item.unregister();
        }
    }

    /**
     * Remove all item/scores in this objective.
     *
     * @throws IllegalStateException if the objective was removed
     */
    public void clearItems() throws IllegalStateException {
        Preconditions.checkState(isShown());

        for (Item item : items.values()) {
            item.unregister();
        }

        items.clear();
    }

    /**
     * Unregister this objective.
     */
    public void unregister() {
        if (isShown()) {
            scoreboard.removeObjective(objectiveName);
            PacketFactory.sendPacket(this, State.REMOVED);
        }
    }

    /**
     * Get the owner of this objective.
     *
     * @return the tracking player
     */
    public Player getOwner() {
        return scoreboard.getPlayer();
    }

    /**
     * Get the scoreboard instance.
     *
     * @return the scoreboard
     */
    public PlayerScoreboard getScoreboard() {
        return scoreboard;
    }
}
