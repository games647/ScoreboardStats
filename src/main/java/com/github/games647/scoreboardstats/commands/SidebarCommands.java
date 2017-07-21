package com.github.games647.scoreboardstats.commands;

import com.github.games647.scoreboardstats.ScoreboardStats;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

/**
 * This class forward all commands to the user commands for a better access
 */
public class SidebarCommands implements TabExecutor {

    private final ScoreboardStats plugin;

    private final Map<String, CommandHandler> commands = Maps.newHashMap();
    private final List<String> subCommands = Lists.newArrayList();

    public SidebarCommands(ScoreboardStats plugin) {
        this.plugin = plugin;

        registerSubCommands();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //default subcommand
        String subCommand = "toggle";
        String[] newArgs = args;
        if (args.length != 0) {
            subCommand = args[0];
            //remove the subcommand from the args list
            newArgs = Arrays.copyOfRange(args, 1, args.length);
        }

        CommandHandler commandHandler = commands.get(subCommand);
        if (commandHandler == null) {
            sender.sendMessage(ChatColor.DARK_RED + "Command not found");
        } else {
            if (commandHandler.hasPermission(sender)) {
                commandHandler.onCommand(sender, subCommand, newArgs);
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            return ImmutableList.of();
        }

        String lastWord = args[args.length - 1];
        List<String> suggestion;
        if (args.length == 1) {
            return subCommands.stream()
                    .filter(subCommand -> StringUtil.startsWithIgnoreCase(subCommand, lastWord))
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .collect(Collectors.toList());
        }

        String subCommand = args[0];
        CommandHandler commandHandler = commands.get(subCommand);
        if (commandHandler != null) {
            //remove the subcommand from the args list
            suggestion = commandHandler.onTabComplete(sender, subCommand, Arrays.copyOfRange(args, 1, args.length));
            if (suggestion != null) {
                //Prevent NPEs and the usage of this method in nearly every handler
                suggestion.sort(String.CASE_INSENSITIVE_ORDER);
            }

            return suggestion;
        }

        return null;
    }

    private void registerSubCommands() {
        register(new ToggleCommand(plugin));
        register(new ReloadCommand(plugin));
    }

    private void register(CommandHandler handler) {
        for (String alias : handler.getAliases()) {
            commands.put(alias, handler);
            subCommands.add(alias);
        }

        String subCommand = handler.getSubCommand();
        commands.put(subCommand, handler);
        subCommands.add(subCommand);
    }
}
