package com.github.games647.scoreboardstats;

import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
import com.github.games647.scoreboardstats.variables.UnknownVariableException;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class SbManager {

    private final ReplaceManager replaceManager = new ReplaceManager();

    public void createScoreboard(final Player player) {
        if (!checkState(player)) {
            return;
        }

        if ((player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null)
                && !"ScoreboardStatsT".equals(player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getName())) {
            return;
        }

        final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        final Objective objective = scoreboard.registerNewObjective("ScoreboardStats", "dummy");
        objective.setDisplayName(ChatColor.translateAlternateColorCodes(ChatColor.COLOR_CHAR, Settings.getTitle()));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        if (player.isOnline()) {
            try {
                player.setScoreboard(scoreboard);
            } catch (IllegalStateException ex) {
                return; //Silent
            }

            sendUpdate(player, true);
            if (Settings.isTempScoreboard()) {
                Bukkit.getScheduler().runTaskLater(ScoreboardStats.getInstance(), new Runnable() {

                    @Override
                    public void run() {
                        if (player.isOnline()) {
                            createTopListScoreboard(player);
                        }
                    }
                }, Settings.getTempShow() * 20L);
            }
        }
    }

    public void createTopListScoreboard(final Player player) {
        final Scoreboard scoreboard = player.getScoreboard();
        if (!checkState(player)
                || (scoreboard.getObjective(DisplaySlot.SIDEBAR) == null)
                || !scoreboard.getObjective(DisplaySlot.SIDEBAR).getName().startsWith("ScoreboardStats")) {
            return;
        }

        if (scoreboard.getObjective("ScoreboardStatsT") != null) {
            //to remove the old scores
            scoreboard.getObjective("ScoreboardStatsT").unregister();
        }

        final Map<String, Integer> top = Database.getTop();
        final String color = Settings.getTempColor();

        final Objective objective = scoreboard.registerNewObjective("ScoreboardStatsT", "dummy");
        objective.setDisplayName(ChatColor.translateAlternateColorCodes(ChatColor.COLOR_CHAR, Settings.getTempTitle()));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        if (player.isOnline()) {
            try {
                player.setScoreboard(scoreboard);
            } catch (IllegalStateException ex) {
                return; //Silent
            }

            for (Map.Entry<String, Integer> entry : top.entrySet()) {
                sendScore(objective, String.format("%s%s", color, checkLength(entry.getKey())), entry.getValue(), false);
            }

            Bukkit.getScheduler().runTaskLater(ScoreboardStats.getInstance(), new Runnable() {
                @Override
                public void run() {
                    createScoreboard(player);
                }
            }, Settings.getTempDisapper() * 20L);
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

    public void sendUpdate(Player player, boolean complete) {
        final Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        if (player.hasPermission("scoreboardstats.use")
                && "ScoreboardStats".equals(objective.getName())
                && !ScoreboardStats.getInstance().getHidelist().contains(player.getName())) {
            final Iterator<Map.Entry<String, String>> iter = Settings.getItems();
            while (iter.hasNext()) {
                final Map.Entry<String, String> entry = iter.next();
                final String title = entry.getKey();
                final String variable = entry.getValue();
                try {
                    final int score = replaceManager.getScore(player, variable);
                    sendScore(objective, title, score, complete);
                } catch (UnknownVariableException ex) {
                    iter.remove();

                    final Logger logger = ScoreboardStats.getInstance().getLogger();
                    logger.info(Language.get("unknownVariable", variable));
                }
            }
        }
    }

    public void sendScore(Objective objective, String title, int value, boolean complete) {
        final Score score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.translateAlternateColorCodes('&', title)));
        //Have to use this because the score wouldn't send otherwise
        if (complete && (value == 0)) {
            score.setScore(-1);
        }

        score.setScore(value);
    }

    public void regAll() {
        Bukkit.getScheduler().runTaskAsynchronously(ScoreboardStats.getInstance(), new Runnable() {
            @Override
            public void run() {
                final boolean ispvpstats = Settings.isPvpStats();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.isOnline()) {
                        if (ispvpstats) {
                            Database.loadAccount(player.getName());
                        }

                        createScoreboard(player);
                    }
                }
            }
        });
    }

    public void unregisterAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOnline()) {
                player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
            }
        }
    }

    private boolean checkState(Player player) {
        return player.hasPermission("scoreboardstats.use")
                && !ScoreboardStats.getInstance().getHidelist().contains(player.getName())
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
}
