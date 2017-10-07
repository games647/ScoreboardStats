package com.github.games647.scoreboardstats.commands;

import com.github.games647.scoreboardstats.RefreshTask;
import com.github.games647.scoreboardstats.ScoreboardStats;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

/**
 * Change the visibility of the scoreboard
 */
public class ToggleCommand extends CommandHandler {

    private static final String TOGGLE_MSG = "§2Toggling the scoreboard";

    public ToggleCommand(ScoreboardStats plugin) {
        super("toggle", "&aToggles the sidebar", plugin, "hide", "show");
    }

    @Override
    public void onCommand(CommandSender sender, String subCommand, String... args) {
        if (!(sender instanceof Player)) {
            //the console cannot have a scoreboard
            sender.sendMessage("§4This command can only be executed by Players");
            return;
        }

        //We checked that it can only be players
        Player player = (Player) sender;
        RefreshTask refreshTask = plugin.getRefreshTask();
        if (refreshTask.contains(player)) {
            if ("hide".equals(subCommand) || "toggle".equals(subCommand)) {
                refreshTask.remove(player);
                player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                player.sendMessage(TOGGLE_MSG);
            }
        } else if ("show".equals(subCommand) || "toggle".equals(subCommand)) {
            player.sendMessage(TOGGLE_MSG);
            refreshTask.addToQueue(player);
        }
    }
}
