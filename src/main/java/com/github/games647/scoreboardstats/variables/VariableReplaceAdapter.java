package com.github.games647.scoreboardstats.variables;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.bukkit.plugin.Plugin;

/**
 * Represents a variable replacer as an abstract class.
 *
 * @param <T> the type of your plugin
 */
public abstract class VariableReplaceAdapter<T extends Plugin> implements VariableReplacer {

    private final T plugin;

    private final boolean global;
    private final boolean async;

    private String description;
    private boolean enabled;

    /**
     * Initialize the replacer with the default values
     *
     * @param plugin associated plugin instance
     */
    public VariableReplaceAdapter(T plugin) {
        this(false, false, "No description", plugin);
    }

    /**
     * Initialize the replacer
     *
     * @param global is the value the same for all players or does the replacer needs a specific player
     * @param async is this plugin thread safe
     * @param description description of all variables of this plugin
     * @param plugin associated plugin instance
     */
    public VariableReplaceAdapter(boolean global, boolean async, String description, T plugin) {
        this.global = global;
        this.async = async;
        this.description = description;

        this.plugin = plugin;
    }

    /**
     * Check whether the replacer
     *
     * @return whether the variable values are the same for all players
     */
    public boolean isGlobal() {
        return global;
    }

    /**
     * Check whether this plugin is called async. This means that it's
     * thread-safe.
     *
     * @return whether the replacer is called async
     */
    public boolean isAsync() {
        return async;
    }

    /**
     * Get the default variable descriptions of all variables
     *
     * @return description of all variables of this plugin
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the replacer description
     *
     * @param newDescription
     */
    public void setDescription(String newDescription) {
        this.description = newDescription;
    }

    /**
     * Get the plugin associated to this replacer
     *
     * @return the associated plugin
     */
    public T getPlugin() {
        return plugin;
    }

    /**
     * Check whether the replacer is enabled
     *
     * @return whether the replacer is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Disable the replacer
     */
    public void disable() {
        enabled = false;
    }

    /**
     * Enables the replacer
     */
    public void enable() {
        enabled = true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
