package com.github.games647.scoreboardstats.scoreboard;

import java.util.Optional;

import org.bukkit.scoreboard.DisplaySlot;

/**
 * Represent the three different sides a scoreboard objective can have
 * <p>
 * Protocol specifications can be found here http://wiki.vg/Protocol
 */
class SlotTransformer {

    private SlotTransformer() {
        //singleton
    }

    /**
     * Get the enum from his id
     *
     * @param slotId the id
     * @return the representing enum or null if the id isn't valid
     */
    public static Optional<DisplaySlot> fromId(int slotId) {
        switch (slotId) {
            case 0:
                return Optional.of(DisplaySlot.PLAYER_LIST);
            case 1:
                return Optional.of(DisplaySlot.SIDEBAR);
            case 2:
                return Optional.of(DisplaySlot.BELOW_NAME);
            default:
                return Optional.empty();
        }
    }
}
