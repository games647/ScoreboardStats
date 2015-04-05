package com.github.games647.scoreboardstats.variables;

import com.google.common.collect.Lists;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Represents a wrapper in order to support the usage of the old Replaceable
 */
@SuppressWarnings("deprecation")
public class LegacyReplaceWrapper extends VariableReplaceAdapter<Plugin> {

    private final Replaceable oldReplacer;
    private List<String> variables = Lists.newArrayList();

    public LegacyReplaceWrapper(Plugin plugin, Replaceable oldReplacer) {
        super(plugin);

        this.oldReplacer = oldReplacer;
    }

    @Override
    public List<String> getVariables() {
        return variables;
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
        return oldReplacer.toString();
    }
}
