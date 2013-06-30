package com.github.games647.scoreboardstats.commands;

import com.github.games647.variables.Message;
import com.github.games647.variables.Permissions;

public final class ReloadCommand implements org.bukkit.command.CommandExecutor {

    @Override
    public boolean onCommand(final org.bukkit.command.CommandSender cs, final org.bukkit.command.Command cmd, final String label, final String[] args) {
        if (!cs.hasPermission(Permissions.RELOAD_PERMISSION)) {
            cs.sendMessage(Message.PERMISSION_DENIED);
            return true;
        }

        com.github.games647.scoreboardstats.ScoreboardStats.getInstance().onReload();
        cs.sendMessage(Message.RELOAD_SUCCESS);
        return true;
    }
}
