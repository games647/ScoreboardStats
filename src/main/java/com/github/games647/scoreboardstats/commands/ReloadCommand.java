package com.github.games647.scoreboardstats.commands;

import com.github.games647.scoreboardstats.Lang;
import com.github.games647.scoreboardstats.ScoreboardStats;

import org.bukkit.command.CommandSender;

public class ReloadCommand extends CommandHandler {

    public ReloadCommand(ScoreboardStats plugin) {
        super("reload", plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String subCommand, String[] args) {
        plugin.onReload();
        sender.sendMessage(Lang.get("onReload"));
    }
}
