package com.github.games647.scoreboardstats;

import java.util.Collection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

/*
 * This class forward all comands to the user commands for a better access
 */
public class SidebarCommands implements CommandExecutor {

    private final ScoreboardStats plugin;

    public SidebarCommands(ScoreboardStats plugin) {
        this.plugin = plugin;
    }

    //Forward all commands
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

        final String name = commandSender.getName();
        final Player player = (Player) commandSender;
        final Collection<String> list = plugin.getHidelist();
        if (list.contains(name)) {
            list.remove(name);
            plugin.getScoreboardManager().createScoreboard(player);
        } else {
            list.add(name);
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        }

        commandSender.sendMessage(Lang.get("onToggle"));
        return true;
    }

    private boolean onReloadCommand(CommandSender commandSender) {
        if (!commandSender.hasPermission("scoreboardstats.reload")) {
            commandSender.sendMessage(Lang.get("noPermission"));
            return true;
        }

        ScoreboardStats.getInstance().onReload();
        commandSender.sendMessage(Lang.get("onReload"));
        return true;
    }
}
