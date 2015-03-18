package com.github.games647.scoreboardstats.variables;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Represents a wrapper in order to support the usage of the old Replaceable
 */
public class LegacyReplaceWrapper extends VariableReplaceAdapter<Plugin> {

    private final Replaceable oldReplacer;

    public LegacyReplaceWrapper(Plugin plugin, @SuppressWarnings("deprecation") Replaceable oldReplacer) {
        super(plugin);

        this.oldReplacer = oldReplacer;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        final int scoreValue = oldReplacer.getScoreValue(player, '%' + variable + '%');
        if (scoreValue != Replaceable.UNKOWN_VARIABLE) {
            replaceEvent.setScore(scoreValue);
        }
    }

    @Override
    public int hashCode() {
        //make it possible to remove them using the Replaceable instance
        return oldReplacer.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        //make it possible to remove them using the Replaceable instance
        return oldReplacer.equals(other);
    }

    @Override
    public String toString() {
        return "LegacyReplaceWrapper{" + "oldReplacer=" + oldReplacer + '}';
    }
}
