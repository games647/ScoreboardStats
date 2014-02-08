package com.github.games647.scoreboardstats.commands;

import com.github.games647.scoreboardstats.Lang;
import com.github.games647.scoreboardstats.ScoreboardStats;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Handle the Reload command for the plugin.
 */
public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, final String label, final String[] args) {
        if (!commandSender.hasPermission("scoreboardstats.reload")) {
            commandSender.sendMessage(Lang.get("noPermission"));
            return true;
        }

        ScoreboardStats.getInstance().onReload();
        commandSender.sendMessage(Lang.get("onReload"));
        return true;
    }
}
