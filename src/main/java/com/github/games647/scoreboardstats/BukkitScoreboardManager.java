package com.github.games647.scoreboardstats;

import com.github.games647.scoreboardstats.config.Lang;
import com.github.games647.scoreboardstats.config.Settings;
import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.UnknownVariableException;

import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * Managing the scoreboard access.
 *
 * @see com.github.games647.scoreboardstats.protocol.PacketSbManager
 */
public class BukkitScoreboardManager extends SbManager {

    private static final String CRITERIA = "dummy";

    private final boolean oldBukkit;

    /**
     * Initialize scoreboard manager.
     *
     * @param pluginInstance the ScoreboardStats instance
     */
    public BukkitScoreboardManager(ScoreboardStats pluginInstance) {
        super(pluginInstance);

        oldBukkit = isOldScoreboardAPI();
    }

    @Override
    public void createScoreboard(Player player) {
        final Objective oldObjective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        if (!isValid(player) || oldObjective != null && !TEMP_SB_NAME.equals(oldObjective.getName())) {
            //Check if another scoreboard is showing
            return;
        }

        //Creates a new personal scoreboard and a new objective
        final Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        final Objective objective = board.registerNewObjective(SB_NAME, CRITERIA);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(Settings.getTitle());

        try {
            player.setScoreboard(board);
        } catch (IllegalStateException stateEx) {
            //the player logged out - fail silently
            //https://hub.spigotmc.org/stash/projects/SPIGOT/repos/spigot/browse/
            //CraftBukkit-Patches/0066-Disable-Connected-Check-on-setScoreboard.patch
            return;
        }

        sendUpdate(player, true);
        //Schedule the next tempscoreboard show
        scheduleShowTask(player, true);
    }

    @Override
    public void unregister(Player player) {
        if (player.isOnline()) {
            for (Objective objective : player.getScoreboard().getObjectives()) {
                final String objectiveName = objective.getName();
                if (objectiveName.startsWith(SB_NAME)) {
                    objective.unregister();
                }
            }
        }
    }

    @Override
    public void onUpdate(Player player) {
        final Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        if (objective == null) {
            //The player has no scoreboard so create one
            createScoreboard(player);
        } else {
            sendUpdate(player, false);
        }
    }

    @Override
    public void createTopListScoreboard(Player player) {
        final Objective oldObjective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        if (!isValid(player) || oldObjective == null
                || !oldObjective.getName().startsWith(SB_NAME)) {
            //Check if another scoreboard is showing
            return;
        }

        //remove old scores
        if (TEMP_SB_NAME.equals(oldObjective.getName())) {
            oldObjective.unregister();
        }

        final Scoreboard board = player.getScoreboard();
        final Objective objective = board.registerNewObjective(TEMP_SB_NAME, CRITERIA);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(Settings.getTempTitle());

        try {
            player.setScoreboard(board);
        } catch (IllegalStateException stateEx) {
            //the player logged out - fail silently
            //https://hub.spigotmc.org/stash/projects/SPIGOT/repos/spigot/browse/
            //CraftBukkit-Patches/0066-Disable-Connected-Check-on-setScoreboard.patch
            return;
        }

        //Colorize and send all elements
        for (Map.Entry<String, Integer> entry : plugin.getStatsDatabase().getTop()) {
            final String scoreName = stripLength(Settings.getTempColor() + entry.getKey());
            sendScore(objective, scoreName, entry.getValue(), true);
        }

        //schedule the next normal scoreboard show
        scheduleShowTask(player, false);
    }

    @Override
    public void update(Player player, String itemName, int newScore) {
        final Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard != null) {
            final Objective objective = scoreboard.getObjective(SB_NAME);
            if (objective != null) {
                sendScore(objective, itemName, newScore, false);
            }
        }
    }

    @Override
    protected void sendUpdate(Player player, boolean complete) {
        final Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        //don't override other scoreboards
        if (objective != null && SB_NAME.equals(objective.getName())) {
            final Iterator<Map.Entry<String, String>> iter = Settings.getItems();
            while (iter.hasNext()) {
                final Map.Entry<String, String> entry = iter.next();
                final String title = entry.getKey();
                final String variable = entry.getValue();

                try {
                    final ReplaceEvent replaceEvent = replaceManager.getScore(player, variable, title, 0, complete);
                    if (replaceEvent.isModified()) {
                        sendScore(objective, title, replaceEvent.getScore(), complete);
                    }
                } catch (UnknownVariableException ex) {
                    //Remove the variable becaue we can't replace it
                    iter.remove();
                    plugin.getLogger().info(Lang.get("unknownVariable", variable));
                }
            }
        }
    }

    private void sendScore(Objective objective, String title, int value, boolean complete) {
        final String scoreName = expandScore(title, objective);

        Score score;
        if (oldBukkit) {
            //Bukkit.getOfflinePlayer performance work around
            score = objective.getScore(new FastOfflinePlayer(scoreName));
        } else {
            score = objective.getScore(scoreName);
        }

        if (complete && value == 0) {
            /*
             * Workaround because the value from Bukkit is set as default to zero and Bukkit sends only
             * the packet if the value changes so we have to change it to another value earlier
             */
            score.setScore(1337);
        }

        score.setScore(value);
    }

    private String expandScore(String scoreName, Objective objective) {
        String cleanScoreName = scoreName;
        final int titleLength = cleanScoreName.length();
        if (titleLength > 16) {
            final Scoreboard scoreboard = objective.getScoreboard();
            //Could maybe cause conflicts but .substring could also make conflicts if they have the same beginning
            final String teamId = String.valueOf(cleanScoreName.hashCode());

            Team team = scoreboard.getTeam(teamId);
            if (team == null) {
                team = scoreboard.registerNewTeam(teamId);
                team.setPrefix(cleanScoreName.substring(0, 16));
                if (titleLength > 32) {
                    //we already validated that this one can only be 48 characters long in the Settings class
                    team.setSuffix(cleanScoreName.substring(32));
                    cleanScoreName = cleanScoreName.substring(16, 32);
                } else {
                    cleanScoreName = cleanScoreName.substring(16);
                }

                team.setDisplayName(cleanScoreName);
                //Bukkit.getOfflinePlayer performance work around
                team.addPlayer(new FastOfflinePlayer(cleanScoreName));
            } else {
                cleanScoreName = team.getDisplayName();
            }
        }

        return cleanScoreName;
    }

    private boolean isOldScoreboardAPI() {
        try {
            Objective.class.getDeclaredMethod("getScore", String.class);
        } catch (NoSuchMethodException noSuchMethodEx) {
            //since we have an extra class for it (FastOfflinePlayer)
            //we can fail silently
            return true;
        }

        //We have access to the new method
        return false;
    }
}
