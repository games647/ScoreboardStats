package com.github.games647.scoreboardstats.protocol;

/**
 * Represent the three different sides a scoreboard objective can have
 *
 * Protocol specifications can be found here http://wiki.vg/Protocol
 */
public enum Slot {

    /**
     * The objective will be displayed in the tab/player list
     */
    PLAYER_LIST,

    /**
     * The objective will be displayed on the right middle side of the client
     * screen
     */
    SIDEBAR,

    /**
     * The objective will be displayed under the player name
     */
    BELOW_NAME;

    /**
     * Get the enum from his id
     *
     * @param slotId the id
     * @return the representing enum or null if the id isn't valid
     */
    public static Slot fromId(int slotId) {
        return Slot.values()[slotId];
    }
}
