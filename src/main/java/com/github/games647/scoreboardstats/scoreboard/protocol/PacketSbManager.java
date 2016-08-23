package com.github.games647.scoreboardstats.scoreboard.protocol;

import com.comphenix.protocol.ProtocolLibrary;
import com.github.games647.scoreboardstats.config.Lang;
import com.github.games647.scoreboardstats.SbManager;
import com.github.games647.scoreboardstats.ScoreboardStats;
import com.github.games647.scoreboardstats.config.Settings;
import com.github.games647.scoreboardstats.config.VariableItem;
import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.UnknownVariableException;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;
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

    /**
     * Gets the scoreboard from a player.
     *
     * @param player who owns the scoreboard
     * @return the scoreboard instance
     */
    public PlayerScoreboard getScoreboard(Player player) {
        PlayerScoreboard scoreboard = scoreboards.get(player.getUniqueId());
        if (scoreboard == null) {
            //lazy loading due potenially performance issues
            scoreboard = new PlayerScoreboard(player);
            scoreboards.put(player.getUniqueId(), scoreboard);
        }

        return scoreboard;
    }

    @Override
    public void onUpdate(Player player) {
        Objective sidebar = getScoreboard(player).getSidebarObjective();
        if (sidebar == null) {
            createScoreboard(player);
        } else {
            sendUpdate(player);
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
        if (player.isOnline() && scoreboard != null) {
            scoreboard.getObjectives().stream()
                    .filter(obj -> obj.getName().startsWith(SB_NAME))
                    .forEach(Objective::unregister);
        }
    }

    @Override
    public void createScoreboard(Player player) {
        PlayerScoreboard scoreboard = getScoreboard(player);
        Objective oldObjective = scoreboard.getSidebarObjective();
        if (!isAllowed(player) || oldObjective != null && !TEMP_SB_NAME.equals(oldObjective.getName())) {
            //Check if another scoreboard is showing
            return;
        }

        Objective objective = scoreboard.createSidebarObjective(SB_NAME, Settings.getMainScoreboard().getTitle(), true);
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
                        sendScore(objective, displayText, replaceEvent.getScore());
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
    public void createTopListScoreboard(Player player) {
        PlayerScoreboard scoreboard = getScoreboard(player);
        Objective oldObjective = scoreboard.getSidebarObjective();
        if (!isAllowed(player) || oldObjective == null
                || !oldObjective.getName().startsWith(SB_NAME)) {
            //Check if another scoreboard is showing
            return;
        }

        Objective objective = scoreboard.getObjective(TEMP_SB_NAME);
        if (objective != null) {
            //It's better to send an unregister and let the client handle the remove than sending up to 15
            //item remove packets
            objective.unregister();
        }

        //We are checking if another object is shown. If it's our scoreboard the code will continue to this
        //were the force the replacement, because the scoreboard management in minecraft right now is sync,
        //so we don't expect any crashes by other plugins.
        objective = scoreboard.createSidebarObjective(TEMP_SB_NAME, Settings.getTempTitle(), true);

        //Colorize and send all elements
        for (Map.Entry<String, Integer> entry : plugin.getStatsDatabase().getTop()) {
            String scoreName = stripLength(Settings.getTempColor() + entry.getKey());
            sendScore(objective, scoreName, entry.getValue());
        }

        //schedule the next normal scoreboard show
        scheduleShowTask(player, false);
    }

    @Override
    public void update(Player player, String itemName, int newScore) {
        PlayerScoreboard scoreboard = getScoreboard(player);
        Objective objective = scoreboard.getObjective(SB_NAME);
        if (objective != null) {
            sendScore(objective, itemName, newScore);
        }
    }

    @Override
    protected void sendUpdate(Player player) {
        Objective sidebar = getScoreboard(player).getSidebarObjective();
        if (SB_NAME.equals(sidebar.getName())) {
            Iterator<VariableItem> iter = Settings.getMainScoreboard().getItemsByVariable().values().iterator();
            while (iter.hasNext()) {
                VariableItem variableItem = iter.next();

                String variable = variableItem.getVariable();
                String displayText = variableItem.getDisplayText();
                int score = variableItem.getScore();

                try {
                    ReplaceEvent replaceEvent = replaceManager.getScore(player, variable, displayText, score, false);
                    if (replaceEvent.isModified()) {
                        sendScore(sidebar, displayText, replaceEvent.getScore());
                    }
                } catch (UnknownVariableException ex) {
                    //Remove the variable becaue we can't replace it
                    iter.remove();
                    Settings.getMainScoreboard().getItemsByName().remove(variableItem.getDisplayText());

                    plugin.getLogger().info(Lang.get("unknownVariable", variableItem));
                }
            }
        }
    }

    private void sendScore(Objective objective, String title, int value) {
        Item item = objective.getItem(title);
        if (item == null) {
            objective.registerItem(title, value);
        } else {
            item.setScore(value);
        }
    }
}
