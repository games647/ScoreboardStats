package com.github.games647.scoreboardstats.scoreboard;

/**
 * Represents the state of a scoreboard objective packet
 *
 * Protocol specifications can be found here http://wiki.vg/Protocol
 */
public enum State {

    /**
     * The objective was created
     */
    CREATE,

    /**
     * The objective was removed
     */
    REMOVE,

    /**
     * The display name of the objective was changed
     */
    UPDATE_DISPLAY_NAME;

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
