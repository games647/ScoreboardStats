package com.github.games647.scoreboardstats.commands;

import com.github.games647.scoreboardstats.ScoreboardStats;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.fusesource.jansi.Ansi;

public class DisableCommand implements CommandExecutor {

    private final ScoreboardStats plugin = ScoreboardStats.getInstance();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!cs.hasPermission("scoreboardstats.hide")) {
            cs.sendMessage(Ansi.ansi().fg(Ansi.Color.YELLOW) + "✖ You don't have enough permissions to do that ✖");
            return true;
        }

        if (!(cs instanceof Player)) {
            cs.sendMessage("This command can't be executed by a console");
            return true;
        }

        final String name = cs.getName();
        final Set<String> list = plugin.getHidelist();
        final Player player = (Player) cs;
        if (list.contains(name)) {
            list.remove(name);
            plugin.getScoreboardManager().createScoreboard(player);
        } else {
            list.add(name);
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        }

        cs.sendMessage(ChatColor.GREEN + "Toggling the scoreboard");
        return true;
    }
}
