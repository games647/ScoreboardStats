package com.github.games647.scoreboardstats.protocol;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.bukkit.entity.Player;

/**
 * Represents a scoreboard item with ProtocolLib.
 *
 * @see Objective
 */
@EqualsAndHashCode(doNotUseGetters = true)
@ToString(doNotUseGetters = true)
public final class Item implements Comparable<Item> {

    private final Objective parent;
    private final String scoreName;

    private int score;

    Item(Objective parent, String scoreName, int score) {
        this(parent, scoreName, score, true);
    }

    Item(Objective parent, String scoreName, int score, boolean send) {
        this.parent = parent;

        this.scoreName = scoreName;
        this.score = score;

        if (send) {
            update();
        }
    }

    /**
     * Check if this item is shown to the player.
     *
     * @return whether this item is shown to the player
     */
    public boolean isShown() {
        return parent.items.containsValue(this) && parent.isShown();
    }

    /**
     * Get the unique item name.
     *
     * @return the unique item name
     */
    public String getScoreName() {
        return scoreName;
    }

    /**
     * Get the current score.
     *
     * @return the current score
     */
    public int getScore() {
        return score;
    }

    /**
     * Set the score value.
     *
     * @param score the new value
     * @throws IllegalStateException if the item was removed
     * @throws IllegalStateException if the objective was removed
     */
    public void setScore(int score) throws IllegalStateException {
        Preconditions.checkState(isShown());

        if (this.score != score) {
            this.score = score;
            update();
        }
    }

    /**
     * Unregister this item.
     */
    public void unregister() {
        if (isShown()) {
            getScoreboard().resetScore(scoreName);
            PacketFactory.sendPacket(this, State.REMOVED);
        }
    }

    /**
     * Get the associated parent.
     *
     * @return the objective that hold this item
     */
    public Objective getParent() {
        return this.parent;
    }

    /**
     * Gets the owner of this item and objective.
     *
     * @return the tracking player
     */
    public Player getOwner() {
        return parent.getOwner();
    }

    /**
     * Get the scoreboard instance.
     *
     * @return the scoreboard
     */
    public PlayerScoreboard getScoreboard() {
        return parent.getScoreboard();
    }

    @Override
    public int compareTo(Item other) {
        //Reverse order - first the highest element
        return Ints.compare(other.score, score);
    }

    private void update() {
        PacketFactory.sendPacket(this, State.CREATED);
    }
}
