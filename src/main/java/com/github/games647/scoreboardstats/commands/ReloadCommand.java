package com.github.games647.scoreboardstats.commands;

import com.github.games647.scoreboardstats.ScoreboardStats;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, final String label, final String[] args) {
        if (!cs.hasPermission("scoreboardstats.reload")) {
            cs.sendMessage(ChatColor.DARK_RED + "✖ You don't have enough permissions to do that ✖");
            return true;
        }

        ScoreboardStats.getInstance().onReload();
        cs.sendMessage(ChatColor.GREEN + "✔ The configuration was successfully reloaded ✔");
        return true;
    }
}
