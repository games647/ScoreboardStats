package com.github.games647.scoreboardstats.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/*
 * This class forward all comands to the user commands for a better access
 */
public class SidebarCommand implements CommandExecutor {

    //Forward all commands
    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {
        if (args.length == 0
                || "hide".equalsIgnoreCase(args[0])
                || "toggle".equalsIgnoreCase(args[0])
                || "show".equalsIgnoreCase(args[0])) {
            Bukkit.dispatchCommand(commandSender, "sb:toggle");
            return true;
        }

        if ("reload".equalsIgnoreCase(args[0])) {
            Bukkit.dispatchCommand(commandSender, "sb:reload");
            return true;
        }

        return false;
    }
}
