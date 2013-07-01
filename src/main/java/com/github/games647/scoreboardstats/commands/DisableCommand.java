package com.github.games647.scoreboardstats.commands;

import com.github.games647.scoreboardstats.ScoreboardStats;
import com.github.games647.variables.Message;
import com.github.games647.variables.Permissions;

import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

public final class DisableCommand implements org.bukkit.command.CommandExecutor {

    private final ScoreboardStats plugin = ScoreboardStats.getInstance();

    @Override
    public boolean onCommand(final CommandSender cs, final Command cmd, final String label, final String[] args) {
        if (!cs.hasPermission(Permissions.HIDE_PERMISSION)) {
            cs.sendMessage(Message.PERMISSION_DENIED);
            return true;
        }

        if (!(cs instanceof Player)) {
            cs.sendMessage(Message.NO_CONSOLE);
            return true;
        }

        final String                name = cs.getName();
        final Set<String> list = plugin.hidelist;

        if (list.contains(name)) {
            list.remove(name);
        } else {
            list.add(name);
        }

        ((Player) cs).getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        return true;
    }
}
