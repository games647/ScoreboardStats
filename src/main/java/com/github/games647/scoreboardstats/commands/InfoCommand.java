package com.github.games647.scoreboardstats.commands;

import com.github.games647.scoreboardstats.ScoreboardStats;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class InfoCommand extends CommandHandler {

    private static final String PREFIX = ChatColor.WHITE + "["
            + ChatColor.GOLD + "%name%"
            + ChatColor.WHITE + "]" + ChatColor.RESET;

    public InfoCommand(ScoreboardStats plugin) {
        super("info", plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String subCommand, String[] args) {
        final String pluginVersion = plugin.getDescription().getVersion();

        final ChatColor color = ChatColor.DARK_AQUA;
        final ChatColor highlightColor = ChatColor.AQUA;
        final String message = PREFIX + color + " Running %name% v" + highlightColor + "%version%"
                + highlightColor + ". Use /help" + color + " for help.";
        sender.sendMessage(message.replace("%name%", plugin.getName()).replace("%version%", pluginVersion));
    }
}
