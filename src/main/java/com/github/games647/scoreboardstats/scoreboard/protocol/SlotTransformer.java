package com.github.games647.scoreboardstats.scoreboard.protocol;

import org.bukkit.scoreboard.DisplaySlot;

/**
 * Represent the three different sides a scoreboard objective can have
 *
 * Protocol specifications can be found here http://wiki.vg/Protocol
 */
public class SlotTransformer {

    /**
     * Get the enum from his id
     *
     * @param slotId the id
     * @return the representing enum or null if the id isn't valid
     */
    public static DisplaySlot fromId(int slotId) {
        switch (slotId) {
            case 0:
                return DisplaySlot.PLAYER_LIST;
            case 1:
                return DisplaySlot.SIDEBAR;
            case 2:
                return DisplaySlot.BELOW_NAME;
            default:
                return null;
        }
    }

    private SlotTransformer() {
        //singleton
    }
}
