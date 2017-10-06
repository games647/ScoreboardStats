package com.github.games647.scoreboardstats.scoreboard;

import com.comphenix.protocol.ProtocolLibrary;
import com.github.games647.scoreboardstats.SbManager;
import com.github.games647.scoreboardstats.ScoreboardStats;
import com.github.games647.scoreboardstats.config.Settings;
import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
import com.github.games647.scoreboardstats.variables.UnknownVariableException;
import com.github.games647.scoreboardstats.variables.VariableItem;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * Manage the scoreboards with packet-use
 */
public class PacketSbManager extends SbManager {

    private final Map<UUID, PlayerScoreboard> scoreboards = Maps.newHashMapWithExpectedSize(50);

    /**
     * Creates a new scoreboard manager for the packet system.
     *
     * @param plugin ScoreboardStats instance
     */
    public PacketSbManager(ScoreboardStats plugin) {
        super(plugin);

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketListener(plugin, this));
    }

    public PlayerScoreboard getScoreboard(Player player) {
        return scoreboards
                .computeIfAbsent(player.getUniqueId(), key -> new PlayerScoreboard(player));
    }

    @Override
    public void onUpdate(Player player) {
        if (getScoreboard(player).getSidebarObjective().isPresent()) {
            sendUpdate(player);
        } else {
            createScoreboard(player);
        }
    }

    @Override
    public void unregisterAll() {
        super.unregisterAll();

        scoreboards.clear();
    }

    @Override
    public void unregister(Player player) {
        PlayerScoreboard scoreboard = scoreboards.remove(player.getUniqueId());
        if (scoreboard != null) {
            scoreboard.getObjectives().stream()
                    .filter(obj -> obj.getId().startsWith(SB_NAME))
                    .map(Objective::getId)
                    .forEach(scoreboard::removeObjective);
        }
    }

    @Override
    public void createScoreboard(Player player) {
        PlayerScoreboard scoreboard = getScoreboard(player);
        Optional<Objective> oldObjective = scoreboard.getSidebarObjective();
        if (!isAllowed(player) || oldObjective.map(Objective::getId).map(TEMP_SB_NAME::equals).orElse(false)) {
            //Check if another scoreboard is showing
            return;
        }

        Objective objective = scoreboard.getOrCreateObjective(SB_NAME);
        objective.setDisplayName(Settings.getTempTitle());
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
                    ReplaceManager replaceManager = plugin.getReplaceManager();
                    ReplaceEvent replaceEvent = replaceManager.getScore(player, variable, displayText, defScore, true);
                    if (replaceEvent.isModified()) {
                        objective.setScores(displayText, replaceEvent.getScore());
                    }
                } catch (UnknownVariableException ex) {
                    //Remove the variable because we can't replace it
                    iter.remove();
                    Settings.getMainScoreboard().getItemsByVariable().remove(scoreItem.getVariable());

                    plugin.getLog().info(UNKNOWN_VARIABLE, scoreItem);
                }
            }
        }

        //Schedule the next temp scoreboard show
        scheduleShowTask(player, true);
    }

    @Override
    public void createTopListScoreboard(Player player) {
        PlayerScoreboard scoreboard = getScoreboard(player);
        Optional<Objective> oldObjective = scoreboard.getSidebarObjective();
        if (!isAllowed(player) || oldObjective.map(Objective::getId).map(SB_NAME::equals).orElse(false)) {
            //Check if another scoreboard is showing
            return;
        }

        //Unregister objective instead of sending 15 remove score packets
        scoreboard.getObjective(TEMP_SB_NAME).map(Objective::getId).ifPresent(scoreboard::removeObjective);

        //We are checking if another object is shown. If it's our scoreboard the code will continue to this
        //were the force the replacement, because the scoreboard management in minecraft right now is sync,
        //so we don't expect any crashes by other plugins.
        Objective objective = scoreboard.getOrCreateObjective(TEMP_SB_NAME);
        objective.setDisplayName(Settings.getTempTitle());

        //Colorize and send all elements
        for (Map.Entry<String, Integer> entry : plugin.getStatsDatabase().getTop()) {
            String scoreName = stripLength(Settings.getTempColor() + entry.getKey());
            objective.setScores(scoreName, entry.getValue());
        }

        //schedule the next normal scoreboard show
        scheduleShowTask(player, false);
    }

    @Override
    public void update(Player player, String title, int newScore) {
        getScoreboard(player).getObjective(SB_NAME).ifPresent(objective -> objective.setScores(title, newScore));
    }

    @Override
    public void sendUpdate(Player player) {
        Optional<Objective> sidebar = getScoreboard(player).getSidebarObjective();
        if (sidebar.filter(objective -> SB_NAME.equals(objective.getId())).isPresent()) {
            Iterator<VariableItem> iter = Settings.getMainScoreboard().getItemsByVariable().values().iterator();
            while (iter.hasNext()) {
                VariableItem variableItem = iter.next();

                String variable = variableItem.getVariable();
                String displayText = variableItem.getDisplayText();
                int score = variableItem.getScore();

                try {
                    ReplaceManager replaceManager = plugin.getReplaceManager();
                    ReplaceEvent replaceEvent = replaceManager.getScore(player, variable, displayText, score, false);
                    if (replaceEvent.isModified()) {
                        sidebar.get().setScores(displayText, replaceEvent.getScore());
                    }
                } catch (UnknownVariableException ex) {
                    //Remove the variable because we can't replace it
                    iter.remove();
                    Settings.getMainScoreboard().getItemsByName().remove(variableItem.getDisplayText());

                    plugin.getLog().info(UNKNOWN_VARIABLE, variableItem);
                }
            }
        }
    }
}
