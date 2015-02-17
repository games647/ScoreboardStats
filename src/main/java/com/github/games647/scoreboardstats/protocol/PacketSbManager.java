package com.github.games647.scoreboardstats.protocol;

import com.comphenix.protocol.ProtocolLibrary;
import com.github.games647.scoreboardstats.Lang;
import com.github.games647.scoreboardstats.SbManager;
import com.github.games647.scoreboardstats.ScoreboardStats;
import com.github.games647.scoreboardstats.Settings;
import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.variables.Replaceable;
import com.github.games647.scoreboardstats.variables.UnknownVariableException;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Manage the scoreboards with packet-use
 */
public class PacketSbManager extends SbManager {

    private final Map<Player, PlayerScoreboard> scoreboards = new WeakHashMap<Player, PlayerScoreboard>(50);

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
    public void sendUpdate(Player player) {
        final Objective sidebar = getScoreboard(player).getSidebarObjective();
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
        final PlayerScoreboard scoreboard = scoreboards.get(player);
        for (Objective objective : scoreboard.getObjectives()) {
            final String objectiveName = objective.getName();
            if (objectiveName.startsWith(SB_NAME)) {
                objective.unregister();
            }
        }
    }

    @Override
    public void createScoreboard(Player player) {
        final PlayerScoreboard scoreboard = getScoreboard(player);
        final Objective oldObjective = scoreboard.getSidebarObjective();
        if (!isValid(player) || oldObjective != null && !TEMP_SB_NAME.equals(oldObjective.getName())) {
            //Check if another scoreboard is showing
            return;
        }

        scoreboard.createSidebarObjective(SB_NAME, Settings.getTitle(), true);
        sendUpdate(player, true);

        //Schedule the next tempscoreboard show
        scheduleShowTask(player, true);
    }

    /**
     *
     * @param player who will receive the scoreboard
     */
    @Override
    public void createTopListScoreboard(Player player) {
        final PlayerScoreboard scoreboard = getScoreboard(player);
        final Objective oldObjective = scoreboard.getSidebarObjective();
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
        for (Map.Entry<String, Integer> entry : Database.getTop()) {
            final String color = Settings.getTempColor();
            final String scoreName = color + entry.getKey();
            sendScore(objective, scoreName, entry.getValue());
        }

        //schedule the next normal scoreboard show
        scheduleShowTask(player, false);
    }

    @Override
    public void update(Player player, String itemName, int newScore) {
        final PlayerScoreboard scoreboard = scoreboards.get(player);
        if (scoreboard != null) {
            final Objective objective = scoreboard.getObjective(SB_NAME);
            if (objective != null) {
                sendScore(objective, itemName, newScore);
            }
        }
    }

    @Override
    protected void sendUpdate(Player player, boolean complete) {
        final Objective sidebar = getScoreboard(player).getSidebarObjective();
        if (SB_NAME.equals(sidebar.getName())) {
            final Iterator<Map.Entry<String, String>> iter = Settings.getItems();
            while (iter.hasNext()) {
                final Map.Entry<String, String> entry = iter.next();
                final String title = entry.getKey();
                final String variable = entry.getValue();
                if (!complete && skipList.contains(variable)) {
                    continue;
                }

                try {
                    final int score = replaceManager.getScore(player, variable);
                    if (score == Replaceable.ON_EVENT) {
                        skipList.add(variable);
                        continue;
                    }

                    sendScore(sidebar, title, score);
                } catch (UnknownVariableException ex) {
                    //Remove the variable becaue we can't replace it
                    iter.remove();

                    plugin.getLogger().info(Lang.get("unknownVariable", variable));
                }
            }
        }
    }

    private void sendScore(Objective objective, String title, int value) {
        final String name = stripLength(ChatColor.translateAlternateColorCodes('&', title));
        final Item item = objective.getItem(name);
        if (item == null) {
            objective.registerItem(name, value);
        } else {
            item.setScore(value);
        }
    }
}
