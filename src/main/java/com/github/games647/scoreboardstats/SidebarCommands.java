package com.github.games647.scoreboardstats;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

/**
 * This class forward all comands to the user commands for a better access
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
                || "hide".equalsIgnoreCase(args[0])
                || "toggle".equalsIgnoreCase(args[0])
                || "show".equalsIgnoreCase(args[0])) {
            return onToggleCommand(commandSender);
        }

        if ("reload".equalsIgnoreCase(args[0])) {
            return onReloadCommand(commandSender);
        }

        return false;
    }

    private boolean onToggleCommand(CommandSender commandSender) {
        if (!commandSender.hasPermission("scoreboardstats.hide")) {
            commandSender.sendMessage(Lang.get("noPermission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Lang.get("noConsole"));
            return true;
        }

        final Player player = (Player) commandSender;
        final RefreshTask refreshTask = plugin.getRefreshTask();
        if (refreshTask.contains(player)) {
            refreshTask.remove(player);
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        } else {
            refreshTask.addToQueue(player);
        }

        commandSender.sendMessage(Lang.get("onToggle"));
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
