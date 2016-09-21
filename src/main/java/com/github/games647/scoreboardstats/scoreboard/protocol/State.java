package com.github.games647.scoreboardstats.scoreboard.protocol;

/**
 * Represents the state of a scoreboard packet
 *
 * Protocol specifications can be found here http://wiki.vg/Protocol
 */
public enum State {

    /**
     * The objective or the item was created
     */
    CREATE,

    /**
     * The objective or the item was removed
     */
    REMOVE,

    /**
     * The display name of the objective was changed
     */
    UPDATE;

    /**
     * Get the enum from his id
     *
     * @param stateId the id
     * @return the representing enum or null if the id not valid
     */
    public static State fromId(int stateId) {
        return State.values()[stateId];
    }
}
