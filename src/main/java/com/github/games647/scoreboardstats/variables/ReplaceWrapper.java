package com.github.games647.scoreboardstats.variables;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Represents a wrapper to support the usage of the interface instead
 * of the VariableReplaceAdapter
 */
public class ReplaceWrapper extends VariableReplaceAdapter<Plugin> {

    private final VariableReplacer replacer;

    public ReplaceWrapper(Plugin plugin, VariableReplacer replacer, String... variables) {
        super(plugin, variables);

        this.replacer = replacer;
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        replacer.onReplace(player, variable, replaceEvent);
    }

    @Override
    public int hashCode() {
        //make it possible to remove them using the Replaceable instance
        return replacer.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        //make it possible to remove them using the Replaceable instance
        return replacer.equals(other);
    }

    @Override
    public String toString() {
        return replacer.toString();
    }
}
