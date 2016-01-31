package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.Version;
import com.github.games647.scoreboardstats.variables.UnsupportedPluginException;
import com.github.games647.scoreboardstats.variables.VariableReplaceAdapter;

import org.bukkit.plugin.Plugin;

public abstract class DefaultReplaceAdapter<P extends Plugin> extends VariableReplaceAdapter<P> {

    public DefaultReplaceAdapter(P plugin, String... variables) {
        super(plugin, variables);
    }

    public DefaultReplaceAdapter(P plugin, String description, boolean global, boolean async, boolean constant
            , String... variables) {
        super(plugin, description, global, async, constant, variables);
    }

    public boolean isNewer(String minVersion) {
        String version = getPlugin().getDescription().getVersion();
        return Version.compare(version, minVersion) >= 0;
    }

    public void checkVersionException(String minVersion) {
        String version = getPlugin().getDescription().getVersion();
        if (Version.compare(version, minVersion) < 0) {
            throw new UnsupportedPluginException("You have an outdated version of " + getPlugin().getName()
                + "Please update it");
        }
    }
}
