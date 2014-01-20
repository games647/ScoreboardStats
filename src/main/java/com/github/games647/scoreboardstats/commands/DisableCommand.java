package com.github.games647.scoreboardstats.commands;

import com.github.games647.scoreboardstats.Language;
import com.github.games647.scoreboardstats.ScoreboardStats;

import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

public class DisableCommand implements CommandExecutor {

    private final ScoreboardStats plugin = ScoreboardStats.getInstance();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!cs.hasPermission("scoreboardstats.hide")) {
            cs.sendMessage(Language.get("noPermission"));
            return true;
        }

        if (!(cs instanceof Player)) {
            cs.sendMessage(Language.get("noConsole"));
            return true;
        }

        final String name = cs.getName();
        final Player player = (Player) cs;
        final Set<String> list = plugin.getHidelist();
        if (list.contains(name)) {
            list.remove(name);
            plugin.getScoreboardManager().createScoreboard(player);
        } else {
            list.add(name);
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        }

        cs.sendMessage(Language.get("onToggle"));
        return true;
    }
}
