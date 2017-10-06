package com.github.games647.scoreboardstats.scoreboard;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;

import java.util.Objects;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Represents a scoreboard item with ProtocolLib.
 *
 * @see Objective
 */
public class Item implements Comparable<Item> {

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
            parent.getScoreboard().resetScore(scoreName);
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

    @Override
    public int compareTo(Item other) {
        //Reverse order - first the highest element like the scoreboard in-game
        return Ints.compare(other.score, score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scoreName);
    }

    @Override
    public boolean equals(Object obj) {
        //ignores also null
        if (obj instanceof Item) {
            Item other = (Item) obj;
            return Objects.equals(scoreName, other.scoreName);
        }

        return false;
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
