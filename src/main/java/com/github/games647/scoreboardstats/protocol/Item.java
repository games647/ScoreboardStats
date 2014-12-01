package com.github.games647.scoreboardstats.protocol;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.bukkit.entity.Player;

/**
 * Represents a scoreboard item with ProtocolLib.
 *
 * @see Objective
 */
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
        return exists() && parent.isShown();
    }

    /**
     * Checks if this item exists client-side.
     *
     * @return whether this item exists
     */
    public boolean exists() {
        return parent.items.containsValue(this) && parent.exists();
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
        Preconditions.checkState(exists(), "the parent objective or this item isn't active");

        if (this.score != score) {
            this.score = score;
            update();
        }
    }

    /**
     * Unregister this item.
     */
    public void unregister() {
        if (exists()) {
            getScoreboard().resetScore(scoreName);
            PacketFactory.sendPacket(this, State.REMOVE);
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
        //Reverse order - first the highest element like the scoreboard ingame
        return Ints.compare(other.score, score);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(scoreName, score);
    }

    @Override
    public boolean equals(Object obj) {
        //ignores also null
        if (obj instanceof Item) {
            final Item other = (Item) obj;
            return Objects.equal(scoreName, other.scoreName)
                    && score == other.score;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Sends a new packet
     *
     * @see PacketFactory
     */
    private void update() {
        PacketFactory.sendPacket(this, State.CREATE);
    }
}
