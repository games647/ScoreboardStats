package com.github.games647.scoreboardstats.protocol;

import com.comphenix.protocol.ProtocolLibrary;
import com.github.games647.scoreboardstats.config.Lang;
import com.github.games647.scoreboardstats.SbManager;
import com.github.games647.scoreboardstats.ScoreboardStats;
import com.github.games647.scoreboardstats.config.Settings;
import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.UnknownVariableException;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;

/**
 * Manage the scoreboards with packet-use
 */
public class PacketSbManager extends SbManager {

    private final Map<Player, PlayerScoreboard> scoreboards = new WeakHashMap<>(50);

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
        PlayerScoreboard scoreboard = scoreboards.get(player);
        if (scoreboard == null) {
            //lazy loading due potenially performance issues
            scoreboard = new PlayerScoreboard(player);
            scoreboards.put(player, scoreboard);
        }

        return scoreboard;
    }

    @Override
    public void onUpdate(Player player) {
        Objective sidebar = getScoreboard(player).getSidebarObjective();
        if (sidebar == null) {
            createScoreboard(player);
        } else {
            sendUpdate(player, false);
        }
    }

    @Override
    public void unregisterAll() {
        super.unregisterAll();

        scoreboards.clear();
    }

    @Override
    public void unregister(Player player) {
        PlayerScoreboard scoreboard = scoreboards.get(player);
        if (scoreboard != null) {
            for (Objective objective : scoreboard.getObjectives()) {
                String objectiveName = objective.getName();
                if (objectiveName.startsWith(SB_NAME)) {
                    objective.unregister();
                }
            }
        }
    }

    @Override
    public void createScoreboard(Player player) {
        PlayerScoreboard scoreboard = getScoreboard(player);
        Objective oldObjective = scoreboard.getSidebarObjective();
        if (!isValid(player) || oldObjective != null && !TEMP_SB_NAME.equals(oldObjective.getName())) {
            //Check if another scoreboard is showing
            return;
        }

        scoreboard.createSidebarObjective(SB_NAME, Settings.getTitle(), true);
        sendUpdate(player, true);

        //Schedule the next tempscoreboard show
        scheduleShowTask(player, true);
    }

    @Override
    public void createTopListScoreboard(Player player) {
        PlayerScoreboard scoreboard = getScoreboard(player);
        Objective oldObjective = scoreboard.getSidebarObjective();
        if (!isValid(player) || oldObjective == null
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
        if (scoreboard != null) {
            Objective objective = scoreboard.getObjective(SB_NAME);
            if (objective != null) {
                sendScore(objective, itemName, newScore);
            }
        }
    }

    @Override
    protected void sendUpdate(Player player, boolean complete) {
        Objective sidebar = getScoreboard(player).getSidebarObjective();
        if (SB_NAME.equals(sidebar.getName())) {
            Iterator<Map.Entry<String, String>> iter = Settings.getItems();
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = iter.next();
                String title = entry.getKey();
                String variable = entry.getValue();

                try {
                    ReplaceEvent replaceEvent = replaceManager.getScore(player, variable, title, 0, complete);
                    if (replaceEvent.isModified()) {
                        sendScore(sidebar, title, replaceEvent.getScore());
                    }
                } catch (UnknownVariableException ex) {
                    //Remove the variable becaue we can't replace it
                    iter.remove();

                    plugin.getLogger().info(Lang.get("unknownVariable", variable));
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
