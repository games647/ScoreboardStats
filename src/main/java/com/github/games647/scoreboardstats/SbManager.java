package com.github.games647.scoreboardstats;

import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
import com.github.games647.scoreboardstats.variables.UnknownVariableException;

import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class SbManager {

    protected final ScoreboardStats pluginInstance;
    private final ReplaceManager replaceManager = new ReplaceManager();

    public SbManager(ScoreboardStats pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    public void createScoreboard(Player player) {
        final Objective oldObjective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        if (!checkState(player)
                //Check if another scoreboard is showing
                || oldObjective != null
                && !"ScoreboardStatsT".equals(oldObjective.getName())) {
            return;
        }

        //Creates a new scoreboard and a new objective
        final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        final Objective objective = scoreboard.registerNewObjective("ScoreboardStats", "dummy");
        objective.setDisplayName(Settings.getTitle());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        try {
            player.setScoreboard(scoreboard);

            sendUpdate(player, true);
            //Schedule the next tempscoreboard show
            if (Settings.isTempScoreboard()) {
                Bukkit.getScheduler().runTaskLater(pluginInstance
                        , new NextShowTask(player, true)
                        , Settings.getTempShow() * 20L);
            }
        } catch (IllegalStateException ex) {
            //Silent fail if the player is disonnect while setting the scoreboard
            pluginInstance.getLogger().fine(Language.get("debugException", ex));
        }
    }

    public void sendUpdate(Player player) {
        final Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        if (objective == null) {
            createScoreboard(player);
        } else {
            sendUpdate(player, false);
        }
    }

    public void regAll() {
        final boolean ispvpstats = Settings.isPvpStats();
        for (Player player: Bukkit.getOnlinePlayers()) {
            if (player.isOnline()) {
                if (ispvpstats) {
                    Database.loadAccount(player.getName());
                }

                createScoreboard(player);
                pluginInstance.getRefreshTask().addToQueue(player);
            }
        }
    }

    public void unregisterAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOnline()) {
                player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
            }
        }
    }

    protected void createTopListScoreboard(Player player) {
        final Scoreboard scoreboard = player.getScoreboard();
        final Objective oldObjective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (!checkState(player) || oldObjective == null
                || !oldObjective.getName().startsWith("ScoreboardStats")) {
            //Check if another scoreboard is showing
            return;
        }

        if ("ScoreboardStatsT".equals(oldObjective.getName())) {
            //remove old scores
            scoreboard.getObjective("ScoreboardStatsT").unregister();
        }

        final Objective objective = scoreboard.registerNewObjective("ScoreboardStatsT", "dummy");
        objective.setDisplayName(Settings.getTempTitle());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        try {
            player.setScoreboard(scoreboard);

            final Map<String, Integer> top = Database.getTop();
            //Colorize and send all elements
            for (Map.Entry<String, Integer> entry: top.entrySet()) {
                final String color = Settings.getTempColor();
                final String scoreName = String.format("%s%s", color, checkLength(entry.getKey()));
                sendScore(objective, scoreName, entry.getValue(), false);
            }

            //schedule the next normal scoreboard show
            Bukkit.getScheduler().runTaskLater(pluginInstance
                    , new NextShowTask(player, false)
                    , Settings.getTempDisapper() * 20L);
        } catch (IllegalStateException ex) {
            //Silent fail if the player is disonnect while setting the scoreboard
            pluginInstance.getLogger().fine(Language.get("debugException", ex));
        }
    }

    private void sendUpdate(Player player, boolean complete) {
        final Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        if ("ScoreboardStats".equals(objective.getName())) {
            final Iterator<Map.Entry<String, String>> iter = Settings.getItems();
            while (iter.hasNext()) {
                final Map.Entry<String, String> entry = iter.next();
                final String title = entry.getKey();
                final String variable = entry.getValue();
                try {
                    final int score = replaceManager.getScore(player, variable);
                    sendScore(objective, title, score, complete);
                } catch (UnknownVariableException ex) {
                    //Remove the variable becaue we can't replace it
                    iter.remove();

                    pluginInstance.getLogger().fine(Language.get("debugException", ex));
                    pluginInstance.getLogger().info(Language.get("unknownVariable", variable));
                }
            }
        }
    }

    private void sendScore(Objective objective, String title, int value, boolean complete) {
        final Score score = objective
                .getScore(Bukkit.getOfflinePlayer(ChatColor.translateAlternateColorCodes('&', title)));
        //Have to use this because the score wouldn't send otherwise
        if (complete && value == 0) {
            score.setScore(1337);
        }

        score.setScore(value);
    }

    private boolean checkState(Player player) {
        return player.hasPermission("scoreboardstats.use")
                && !pluginInstance.getHidelist().contains(player.getName())
                && !Settings.isDisabledWorld(player.getWorld());
    }

    private String checkLength(String check) {
        //Because adding the color
        if (check.length() > (16 - 2)) {
            return check.substring(0, 16 - 2);
        } else {
            return check;
        }
    }

    private class NextShowTask implements Runnable {

        private final Player player;
        private final boolean action;

        NextShowTask(Player player, boolean action) {
            this.player = player;
            this.action = action;
        }

        @Override
        public void run() {
            if (action) {
                createTopListScoreboard(player);
            } else {
                createScoreboard(player);
            }
        }
    }
}
