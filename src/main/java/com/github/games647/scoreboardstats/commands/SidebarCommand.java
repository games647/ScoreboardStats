package com.github.games647.scoreboardstats.commands;

import com.github.games647.variables.Commands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class SidebarCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 0
                || args[0].equalsIgnoreCase("hide")
                || args[0].equalsIgnoreCase("toggle")) {
            ((Player) cs).performCommand(Commands.HIDE_COMMAND);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            ((Player) cs).performCommand(Commands.RELOAD_COMMAND);
            return true;
        }

        return false;
    }
}
