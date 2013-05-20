package com.github.games647.commands;

public class ReloadCommand implements org.bukkit.command.CommandExecutor {

    @Override
    public boolean onCommand(final org.bukkit.command.CommandSender cs, final org.bukkit.command.Command cmd, final String label, final String[] args) {
        if (cs.hasPermission("scoreboardstats.admin")) {
            cs.sendMessage("§cYou don't have enough permissions");
            return true;
        }

        com.github.games647.scoreboardstats.ScoreboardStats.onReload();
        cs.sendMessage("§6Successfully reloaded the config");
        return true;
    }

}
