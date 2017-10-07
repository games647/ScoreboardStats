package com.github.games647.scoreboardstats.scoreboard;

import com.comphenix.protocol.ProtocolLibrary;
import com.github.games647.scoreboardstats.SbManager;
import com.github.games647.scoreboardstats.ScoreboardStats;
import com.github.games647.scoreboardstats.config.Settings;
import com.github.games647.scoreboardstats.config.VariableItem;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
import com.github.games647.scoreboardstats.variables.ReplacerException;
import com.github.games647.scoreboardstats.variables.UnknownVariableException;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * Manage the scoreboards with packet-use
 */
public class PacketManager extends SbManager {

    private final Map<UUID, PlayerScoreboard> scoreboards = Maps.newHashMapWithExpectedSize(50);

    /**
     * Creates a new scoreboard manager for the packet system.
     *
     * @param plugin ScoreboardStats instance
     */
    public PacketManager(ScoreboardStats plugin) {
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

        updateVariables(objective, player, true);

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
            updateVariables(sidebar.get(), player, false);
        }
    }

    private void updateVariables(Objective objective, Player player, boolean complete) {
        Iterator<VariableItem> iter = Settings.getMainScoreboard().getItemsByVariable().values().iterator();
        while (iter.hasNext()) {
            VariableItem variableItem = iter.next();

            String variable = variableItem.getVariable();
            String displayText = variableItem.getDisplayText();
            int score = variableItem.getScore();

            try {
                ReplaceManager replaceManager = plugin.getReplaceManager();
                OptionalInt newScore = replaceManager.scoreReplace(player, variable, false);
                if (newScore.isPresent()) {
                    objective.setScores(displayText, newScore.getAsInt());
                }
            } catch (UnknownVariableException ex) {
                //Remove the variable because we can't replace it
                iter.remove();
                Settings.getMainScoreboard().getItemsByName().remove(variableItem.getDisplayText());

                plugin.getLog().info(UNKNOWN_VARIABLE, variableItem);
            } catch (ReplacerException e) {
                iter.remove();
                plugin.getLog().error("Error on replace", e);
            }
        }
    }
}
