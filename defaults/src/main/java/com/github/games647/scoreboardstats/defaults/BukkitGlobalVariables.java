package com.github.games647.scoreboardstats.defaults;

import com.github.games647.scoreboardstats.variables.DefaultReplacer;
import com.github.games647.scoreboardstats.variables.DefaultReplacers;
import com.github.games647.scoreboardstats.variables.ReplacerAPI;

import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.NumberConversions;

/**
 * Replace all Bukkit variables which are the same for players. Currently
 * there is no good way to mark variables as global
 */
@DefaultReplacer
public class BukkitGlobalVariables extends DefaultReplacers<Plugin> {

    public BukkitGlobalVariables(ReplacerAPI replaceManager, Plugin plugin) {
        super(replaceManager, plugin);
    }

    @Override
    public void register() {
        register("tps")
                .scoreSupply(() -> NumberConversions.round(TicksPerSecondTask.getLastTicks()));

        register("online")
                .scoreSupply(() -> Bukkit.getOnlinePlayers().size())
                .eventScore(PlayerJoinEvent.class, () -> Bukkit.getOnlinePlayers().size())
                .eventScore(PlayerQuitEvent.class, () -> Bukkit.getOnlinePlayers().size() - 1);

        register("max_player")
                .scoreSupply(Bukkit::getMaxPlayers)
                .constant();
    }
}
