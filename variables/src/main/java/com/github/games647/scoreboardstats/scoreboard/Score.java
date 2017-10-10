package com.github.games647.scoreboardstats.scoreboard;

/**
 * Represents a scoreboard score item or "line"
 */
public interface Score {

    /**
     * Get the current display name for the scoreboard score
     *
     * @return display name
     */
    String getName();

    /**
     * Score value that is displayed next to the item
     *
     * @return the current score
     */
    int getScore();
}
