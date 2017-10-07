package com.github.games647.scoreboardstats.variables;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

/**
 * Keeps track of plugin disables and enables. It will register default replacers
 * back again or removes replacers of disabled plugins.
 */
class PluginListener implements Listener {

    private final ReplaceManager replaceManager;

    public PluginListener(ReplaceManager replaceManager) {
        this.replaceManager = replaceManager;
    }

    /**
     * Check for disabled plugin to remove the associated replacer
     *
     * @param disableEvent the disable event
     */
    @EventHandler
    public void onPluginDisable(PluginDisableEvent disableEvent) {
        //Remove the listener if the associated plugin was disabled
        Plugin disablePlugin = disableEvent.getPlugin();

        replaceManager.unregisterAll(disablePlugin);
    }
}
