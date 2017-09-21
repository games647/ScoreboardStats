package com.github.games647.scoreboardstats.scoreboard.bukkit;

import com.github.games647.scoreboardstats.SbManager;
import com.github.games647.scoreboardstats.ScoreboardStats;
import com.github.games647.scoreboardstats.config.Lang;
import com.github.games647.scoreboardstats.config.Settings;
import com.github.games647.scoreboardstats.config.VariableItem;
import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.UnknownVariableException;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Managing the scoreboard access.
 */
public class BukkitScoreboardManager extends SbManager {

    private static final String CRITERIA = "dummy";

    /**
     * Initialize scoreboard manager.
     *
     * @param pluginInstance the ScoreboardStats instance
     */
    public BukkitScoreboardManager(ScoreboardStats pluginInstance) {
        super(pluginInstance);
    }

    @Override
    public void createScoreboard(Player player) {
        Objective oldObjective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        if (!isAllowed(player) || oldObjective != null && !TEMP_SB_NAME.equals(oldObjective.getName())) {
            //Check if another scoreboard is showing
            return;
        }

        //Creates a new personal scoreboard and a new objective
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();

        try {
            player.setScoreboard(board);
        } catch (IllegalStateException stateEx) {
            //the player logged out - fail silently
            //https://hub.spigotmc.org/stash/projects/SPIGOT/repos/spigot/browse/CraftBukkit-Patches/
            // 0066-Disable-Connected-Check-on-setScoreboard.patch
            return;
        }

        Objective objective = board.registerNewObjective(SB_NAME, CRITERIA);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(Settings.getMainScoreboard().getTitle());

        Iterator<VariableItem> iter = Settings.getMainScoreboard().getItemsByName().values().iterator();
        while (iter.hasNext()) {
            VariableItem scoreItem = iter.next();

            String displayText = scoreItem.getDisplayText();
            int defScore = scoreItem.getScore();

            String variable = scoreItem.getVariable();
            if (variable == null) {
                update(player, displayText, defScore);
            } else {
                try {
                    ReplaceEvent replaceEvent = replaceManager.getScore(player, variable, displayText, defScore, true);
                    if (replaceEvent.isModified()) {
                        sendScore(objective, displayText, replaceEvent.getScore(), true);
                    }
                } catch (UnknownVariableException ex) {
                    //Remove the variable becaue we can't replace it
                    iter.remove();
                    Settings.getMainScoreboard().getItemsByVariable().remove(scoreItem.getVariable());

                    plugin.getLogger().info(Lang.get("unknownVariable", scoreItem));
                }
            }
        }

        //Schedule the next tempscoreboard show
        scheduleShowTask(player, true);
    }

    @Override
    public void unregister(Player player) {
        player.getScoreboard().getObjectives().stream()
                .filter(obj -> obj.getName().startsWith(SB_NAME))
                .forEach(Objective::unregister);
    }

    @Override
    public void onUpdate(Player player) {
        Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        if (objective == null) {
            //The player has no scoreboard so create one
            createScoreboard(player);
        } else {
            sendUpdate(player);
        }
    }

    @Override
    public void createTopListScoreboard(Player player) {
        Objective oldObjective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        if (!isAllowed(player) || oldObjective == null || !oldObjective.getName().startsWith(SB_NAME)) {
            //Check if another scoreboard is showing
            return;
        }

        //remove old scores
        if (TEMP_SB_NAME.equals(oldObjective.getName())) {
            oldObjective.unregister();
        }

        Scoreboard board = player.getScoreboard();

        Objective objective = board.registerNewObjective(TEMP_SB_NAME, CRITERIA);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(Settings.getTempTitle());

        //Colorize and send all elements
        plugin.getStatsDatabase().getTop().forEach((entry) -> {
            String scoreName = stripLength(Settings.getTempColor() + entry.getKey());
            sendScore(objective, scoreName, entry.getValue(), true);
        });

        //schedule the next normal scoreboard show
        scheduleShowTask(player, false);
    }

    @Override
    public void update(Player player, String itemName, int newScore) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard != null) {
            Objective objective = scoreboard.getObjective(SB_NAME);
            if (objective != null) {
                sendScore(objective, itemName, newScore, false);
            }
        }
    }

    @Override
    protected void sendUpdate(Player player) {
        Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        //don't override other scoreboards
        if (objective != null && SB_NAME.equals(objective.getName())) {
            Iterator<VariableItem> iter = Settings.getMainScoreboard().getItemsByVariable().values().iterator();
            while (iter.hasNext()) {
                VariableItem variableItem = iter.next();

                String variable = variableItem.getVariable();
                String displayText = variableItem.getDisplayText();
                int score = variableItem.getScore();

                try {
                    ReplaceEvent replaceEvent = replaceManager.getScore(player, variable, displayText, score, false);
                    if (replaceEvent.isModified()) {
                        sendScore(objective, displayText, replaceEvent.getScore(), false);
                    }
                } catch (UnknownVariableException ex) {
                    //Remove the variable because we can't replace it
                    iter.remove();
                    Settings.getMainScoreboard().getItemsByName().remove(variableItem.getDisplayText());

                    plugin.getLogger().info(Lang.get("unknownVariable", variableItem));
                }
            }
        }
    }

    private void sendScore(Objective objective, String title, int value, boolean complete) {
        Score score = objective.getScore(title);
        if (complete && value == 0) {
            /*
             * Workaround because the value from Bukkit is set as default to zero and Bukkit sends only
             * the packet if the value changes so we have to change it to another value earlier
             */
            score.setScore(1337);
        }

        score.setScore(value);
    }
}
