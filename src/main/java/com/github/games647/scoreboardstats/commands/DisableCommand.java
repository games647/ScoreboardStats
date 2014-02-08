package com.github.games647.scoreboardstats.commands;

import com.github.games647.scoreboardstats.Lang;
import com.github.games647.scoreboardstats.ScoreboardStats;

import java.util.Collection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

/**
 * Represents the class for handling a toggle command.
 */
public class DisableCommand implements CommandExecutor {

    private final ScoreboardStats plugin;

    public DisableCommand(ScoreboardStats plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {
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
}
