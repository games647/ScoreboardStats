package com.github.games647.scoreboardstats.commands;

import com.github.games647.scoreboardstats.ScoreboardStats;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Represents a handler for sub commands
 */
abstract class CommandHandler {

    protected final ScoreboardStats plugin;

    private final String permission;
    private final String description;
    private final String subCommand;

    private final List<String> aliases;

    CommandHandler(String command, ScoreboardStats plugin, String... aliases) {
        this(command, "&cNo description", plugin, aliases);
    }

    CommandHandler(String command, String description, ScoreboardStats plugin, String... aliases) {
        this.plugin = plugin;
        this.description = ChatColor.translateAlternateColorCodes('&', description);
        this.permission = plugin.getName().toLowerCase() + ".command." + command;
        this.subCommand = command;

        this.aliases = Arrays.asList(aliases);
    }

    /**
     * Get the main sub command
     *
     * @return the main sub command
     */
    public String getSubCommand() {
        return subCommand;
    }

    /**
     * Get all aliases of this command. All strings in this list can invoke this
     * command
     *
     * @return all aliases
     */
    public Iterable<String> getAliases() {
        return aliases;
    }

    /**
     * Get the description of this command
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the permission of this command
     *
     * @return the permission
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Check if the player has enough permissions to execute this command. It
     * will send a no permission if he doesn't have it.
     *
     * @param sender the executor
     * @return whether the sender has enough permissions
     */
    public boolean hasPermission(CommandSender sender) {
        if (sender.hasPermission(permission)) {
            return true;
        }

        sender.sendMessage("§4 You don't have enough permissions to do that");
        return false;
    }

    /**
     * Executes the given subcommand
     *
     * @param sender     Source of the command
     * @param subCommand Command which was executed
     * @param args       The arguments passed to the command, including final partial argument to be completed
     */
    public abstract void onCommand(CommandSender sender, String subCommand, String... args);

    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender     Source of the command
     * @param subCommand Command which was executed
     * @param args       The arguments passed to the command, including final partial argument to be completed
     * @return A List of possible completions for the final argument, or null to default to the command executor
     */
    public List<String> onTabComplete(CommandSender sender, String subCommand, String... args) {
        //default null -> bukkit will handle this with a list of only players
        return null;
    }
}
