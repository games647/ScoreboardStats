package com.github.games647.scoreboardstats.variables;

import org.bukkit.plugin.Plugin;

/**
 * Represents a default replacers instance that will register default variables.
 *
 * @param <T> plugin class for easier access to the plugin field without casting
 */
public abstract class DefaultReplacers<T extends Plugin> {

    protected final ReplacerAPI replaceManager;
    protected final T plugin;

    public DefaultReplacers(ReplacerAPI replaceManager, T plugin) {
        this.replaceManager = replaceManager;
        this.plugin = plugin;
    }

    /**
     * Register all variables that this class can manage
     */
    public abstract void register();

    /**
     * Shortcut method to register the variables without the boilerplate of adding the plugin instance and registering
     * it to the manager.
     *
     * @param variable variable name like "online"
     * @return the replacer responsible for this single variable
     */
    protected Replacer register(String variable) {
        Replacer replacer = new Replacer(plugin, variable);
        replaceManager.register(replacer);
        return replacer;
    }
}
