package com.github.games647.scoreboardstats.commands;

import com.github.games647.scoreboardstats.ScoreboardStats;
import com.github.games647.variables.Message;
import com.github.games647.variables.Permissions;
import org.bukkit.entity.Player;

public final class DisableCommand implements org.bukkit.command.CommandExecutor {

    private final ScoreboardStats plugin;

    public DisableCommand(ScoreboardStats instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(org.bukkit.command.CommandSender cs, org.bukkit.command.Command cmd, String label, String[] args) {
        if (!cs.hasPermission(Permissions.HIDE_PERMISSION)) {
            cs.sendMessage(Message.PERMISSION_DENIED);
            return true;
        }

        if (!(cs instanceof Player)) {
            cs.sendMessage(Message.NO_CONSOLE);
            return true;
        }

        final String                name = cs.getName();
        final java.util.Set<String> list = plugin.hidelist;

        if (list.contains(name)) {
            list.remove(name);
        } else {
            list.add(name);
        }

        ((Player) cs).getScoreboard().clearSlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);
        return true;
    }
}
