package com.github.games647.scoreboardstats.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SidebarCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 0
                || "hide"   .equalsIgnoreCase(args[0])
                || "toggle" .equalsIgnoreCase(args[0])
                || "show"   .equalsIgnoreCase(args[0])) {
            ((Player) cs).performCommand("sb:toggle");
            return true;
        }

        if ("reload".equalsIgnoreCase(args[0])) {
            ((Player) cs).performCommand("sb:reload");
            return true;
        }

        return false;
    }
}
