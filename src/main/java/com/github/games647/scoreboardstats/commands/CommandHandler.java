package com.github.games647.scoreboardstats.commands;

import com.github.games647.scoreboardstats.Lang;
import com.github.games647.scoreboardstats.ScoreboardStats;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public abstract class CommandHandler {

    protected final ScoreboardStats plugin;

    private final String permission;
    private final String description;
    private final String subCommand;

    private final List<String> aliases;

    public CommandHandler(String command, ScoreboardStats plugin, String... aliases) {
        this(command, "&cNo description", plugin, aliases);
    }

    public CommandHandler(String command, String description, ScoreboardStats plugin, String... aliases) {
        this.plugin = plugin;
        this.description = ChatColor.translateAlternateColorCodes('&', description);
        this.permission = plugin.getName().toLowerCase() + ".commands." + command;
        this.subCommand = command;

        this.aliases = Arrays.asList(aliases);
    }

    public String getSubCommand() {
        return subCommand;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }

    public String getPermission() {
        return permission;
    }

    public boolean hasPermission(CommandSender sender) {
        if (sender.hasPermission(permission)) {
            return true;
        }

        sender.sendMessage(Lang.get("noPermission"));
        return false;
    }

    public abstract void onCommand(CommandSender sender, String subCommand, String[] args);

    public List<String> onTabComplete(CommandSender sender, String subCommand, String[] args) {
        return null;
    }
}
