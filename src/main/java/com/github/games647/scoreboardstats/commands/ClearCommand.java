package com.github.games647.scoreboardstats.commands;

public class ClearCommand implements org.bukkit.command.CommandExecutor {

    @Override
    public boolean onCommand(final org.bukkit.command.CommandSender cs, final org.bukkit.command.Command cmd, final String label, final String[] args) {
        if (cs.hasPermission("scoreboardstats.admin")) {
            cs.sendMessage("§cYou don't have enough permissions");
            return true;
        }

        com.github.games647.scoreboardstats.pvpstats.Database.clearTable();
        cs.sendMessage("§6You have successfully cleared the table");
        return true;
    }

}
