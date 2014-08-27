package com.github.games647.scoreboardstats;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

/**
 * This class forward all commands to the user commands for a better access
 */
public class SidebarCommands implements CommandExecutor {

    private final ScoreboardStats plugin;

    SidebarCommands(ScoreboardStats plugin) {
        this.plugin = plugin;
    }

    /**
     * Executes the given command, returning its success
     *
     * @param commandSender Source of the command
     * @param cmd Command which was executed
     * @param label Alias of the command which was used
     * @param args Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {
        if (args.length == 0
                || "toggle".equalsIgnoreCase(args[0])) {
            return onToggleCommand(commandSender, "toggle");
        }

        if ("show".equalsIgnoreCase(args[0])
                || "on".equalsIgnoreCase(args[0])) {
            onToggleCommand(commandSender, "show");
        }

        if ("hide".equalsIgnoreCase(args[0])
                || "off".equalsIgnoreCase(args[0])) {
            onToggleCommand(commandSender, "hide");
        }

        if ("reload".equalsIgnoreCase(args[0])) {
            return onReloadCommand(commandSender);
        }

        //there is no command, so send the usage message. Maybe implements a help page
        return false;
    }

    private boolean onToggleCommand(CommandSender commandSender, String newState) {
        if (!commandSender.hasPermission("scoreboardstats.hide")) {
            commandSender.sendMessage(Lang.get("noPermission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            //the console can't have a scoreboard
            commandSender.sendMessage(Lang.get("noConsole"));
            return true;
        }

        //We checked that it can only be players
        final Player player = (Player) commandSender;
        final RefreshTask refreshTask = plugin.getRefreshTask();
        if (refreshTask.contains(player)) {
            if ("hide".equals(newState) || "toggle".equals(newState)) {
                refreshTask.remove(player);
                player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                commandSender.sendMessage(Lang.get("onToggle"));
            }
        } else if ("show".equals(newState) || "toggle".equals(newState)) {
            commandSender.sendMessage(Lang.get("onToggle"));
            refreshTask.addToQueue(player);
        }

        return true;
    }

    private boolean onReloadCommand(CommandSender commandSender) {
        if (!commandSender.hasPermission("scoreboardstats.reload")) {
            commandSender.sendMessage(Lang.get("noPermission"));
            return true;
        }

        plugin.onReload();
        commandSender.sendMessage(Lang.get("onReload"));
        return true;
    }
}
