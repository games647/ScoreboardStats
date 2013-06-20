package com.github.games647.scoreboardstats.scoreboard;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang.Validate;
import org.bukkit.scoreboard.DisplaySlot;

public final class SObjective {

    private DisplaySlot displayslot;

    private final   String      objectivename; //Should be only under 16 characters
    private         String      displayname; //Can me under 32 characters long

    private int disabled; // 0 to create the scoreboard. 1 to remove the scoreboard. 2 to update the display text. TODO: Check these values
    private final Map<String, SScore> scores = new ConcurrentHashMap<String, SScore>(10); // Never should be more than 15

    public SObjective(final DisplaySlot displayslot, final String objectivename, final String displayname) {
        Validate.notNull(displayslot, "Display slot can't be null");
        Validate.isTrue(objectivename.length() > 16, "The objective name can't be longer than 16 characters.");
        Validate.isTrue(displayname.length() > 32, "The display name can't be longer than 32 characters.");

        this.displayslot    = displayslot;
        this.objectivename  = objectivename;
        this.displayname    = displayname;
    }

    public DisplaySlot getDisplayslot() {
        return displayslot;
    }

    public void setDisplayslot(final DisplaySlot displayslot) {
        Validate.notNull(displayslot, "Display slot can't be null");
        this.displayslot = displayslot;
    }

    public String getObjectivename() {
        return objectivename;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(final String displayname) {
        Validate.isTrue(displayname.length() > 32, "The display name can't be longer than 32 characters.");
        this.displayname = displayname;
    }
}
