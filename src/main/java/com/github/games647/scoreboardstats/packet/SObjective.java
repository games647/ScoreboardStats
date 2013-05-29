package com.github.games647.scoreboardstats.packet;

import java.util.List;
import org.bukkit.scoreboard.DisplaySlot;

public final class SObjective  {

    private DisplaySlot displayslot;
    private String objectivename;
    private String displayname;
    private List<SScore> scores; // Never should be more than 15

    public SObjective(final DisplaySlot displayslot, final String objectivename, final String displayname) {
        this.displayslot = displayslot;
        this.objectivename = objectivename;
        this.displayname = displayname;
    }

    public SObjective(final String displayname) {
        this.displayname = displayname;
        this.objectivename = displayname;
    }

    public DisplaySlot getDisplayslot() {
        return displayslot;
    }

    public void setDisplayslot(final DisplaySlot displayslot) {
        this.displayslot = displayslot;
    }

    public String getObjectivename() {
        return objectivename;
    }

    public void setObjectivename(final String objectivename) {
        this.objectivename = objectivename;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(final String displayname) {
        this.displayname = displayname;
    }
}
