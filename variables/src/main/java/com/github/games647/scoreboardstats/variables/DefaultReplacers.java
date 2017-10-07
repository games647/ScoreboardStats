package com.github.games647.scoreboardstats.variables;

import org.bukkit.plugin.Plugin;

public abstract class DefaultReplacers<T extends Plugin> {

    protected final ReplacerAPI replaceManager;
    protected final T plugin;

    public DefaultReplacers(ReplacerAPI replaceManager, T plugin) {
        this.replaceManager = replaceManager;
        this.plugin = plugin;
    }

    public abstract void register();

    protected Replacer register(String variable) {
        return new Replacer(plugin, variable);
    }
}
