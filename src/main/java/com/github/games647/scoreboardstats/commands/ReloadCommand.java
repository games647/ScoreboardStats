package com.github.games647.scoreboardstats.commands;

import com.github.games647.scoreboardstats.Language;
import com.github.games647.scoreboardstats.ScoreboardStats;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, final String label, final String[] args) {
        if (!cs.hasPermission("scoreboardstats.reload")) {
            cs.sendMessage(Language.get("noPermission"));
            return true;
        }

        ScoreboardStats.getInstance().onReload();
        cs.sendMessage(Language.get("onReload"));
        return true;
    }
}
