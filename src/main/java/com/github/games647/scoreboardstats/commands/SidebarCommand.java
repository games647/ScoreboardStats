package com.github.games647.scoreboardstats.commands;

import com.github.games647.variables.Commands;

public final class SidebarCommand implements org.bukkit.command.CommandExecutor {

    @Override
    public boolean onCommand(org.bukkit.command.CommandSender cs, org.bukkit.command.Command cmd, String label, String[] args) {
        if (args.length == 0
                || args[0].equalsIgnoreCase("hide")
                || args[0].equalsIgnoreCase("toggle")) {
            ((org.bukkit.entity.Player) cs).performCommand(Commands.HIDE_COMMAND);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            ((org.bukkit.entity.Player) cs).performCommand(Commands.RELOAD_COMMAND);
            return true;
        }

        return false;
    }
}
