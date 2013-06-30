package com.github.games647.scoreboardstats.commands;

import com.github.games647.variables.Commands;

public final class SidebarCommand implements org.bukkit.command.CommandExecutor {

    @Override
    public boolean onCommand(org.bukkit.command.CommandSender cs, org.bukkit.command.Command cmd, String label, String[] args) {
        if (args.length == 0
                || "hide"   .equalsIgnoreCase(args[0])
                || "toggle" .equalsIgnoreCase(args[0])) {
            ((org.bukkit.entity.Player) cs).performCommand(Commands.HIDE_COMMAND);
            return true;
        }

        if ("reload".equalsIgnoreCase(args[0])) {
            ((org.bukkit.entity.Player) cs).performCommand(Commands.RELOAD_COMMAND);
            return true;
        }

        return false;
    }
}
