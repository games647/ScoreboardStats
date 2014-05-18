package com.github.games647.scoreboardstats.protocol;

/**
 * Represents the state of a scoreboard packet
 */
public enum State {

    /**
     * The objective or the item was created
     */
    CREATED,

    /**
     * The objective or the item was removed
     */
    REMOVED,

    /**
     * The display name of the objective was changed
     */
    UPDATE_TITLE;

    /**
     * Get the enum from his id
     *
     * @param id the id
     * @return the representing enum or null if the id not valid
     */
    public static State fromId(int id) {
        return State.values()[id];
    }
}
