package com.github.games647.scoreboardstats.variables;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.bukkit.plugin.Plugin;

/**
 * Represents a variable replacer as an abstract class.
 *
 * @param <T> the type of your plugin
 */
public abstract class VariableReplaceAdapter<T extends Plugin> implements VariableReplacer {

    private final T plugin;

    private final boolean async;
    private final boolean global;
    private final boolean constant;
    private final String description;

    private final String[] variables;

    private boolean enabled;

    /**
     * Initialize the replacer with the default values
     *
     * @param plugin associated plugin instance
     * @param variables to replaced variables
     */
    public VariableReplaceAdapter(T plugin, String... variables) {
        this(plugin, "&cNo description", false, false, false, variables);
    }

    /**
     * Initialize the replacer
     *
     * @param global is the value the same for all players or does the replacer needs a specific player
     * @param async is this plugin thread safe
     * @param constant if the variable is updated based on events
     * @param description description of all variables of this plugin
     * @param plugin associated plugin instance
     * @param variables to replaced variables <b>without the variable identifiers (%)</b>
     */
    public VariableReplaceAdapter(T plugin, String description, boolean global, boolean async, boolean constant
            , String... variables) {
        this.plugin = plugin;
        this.async = async;
        this.global = global;
        this.constant = constant;

        this.description = description;

        this.variables = variables;
    }

    /**
     * Get all variables from this replacer
     *
     * @return all variables which can be replaced
     */
    public List<String> getVariables() {
        return Arrays.asList(variables);
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
     * Check whether the variables of this replacer have the same results for all
     * players
     *
     * @return whether the variable values are the same for all players
     */
    public boolean isGlobal() {
        return global;
    }

    /**
     * Check whether the variables of this replacer are updated based on events
     *
     * @return whether the variables of this replacer are updated based on events
     */
    public boolean isConstant() {
        return constant;
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
