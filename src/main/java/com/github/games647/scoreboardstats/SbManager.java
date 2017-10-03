package com.github.games647.scoreboardstats;

import com.github.games647.scoreboardstats.config.Settings;
import com.github.games647.scoreboardstats.scoreboard.DelayedShowTask;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Manage the scoreboard access.
 */
public abstract class SbManager {

    public static final String UNKNOWN_VARIABLE = "Cannot find variable with name: ({}) Maybe you misspelled it or the replacer isn't available yet";

    protected static final String SB_NAME = "Stats";
    protected static final String TEMP_SB_NAME = SB_NAME + 'T';

    private static final int MAX_ITEM_LENGTH = 16;

    protected final ScoreboardStats plugin;

    private final String permission;

    public SbManager(ScoreboardStats plugin) {
        this.plugin = plugin;
        this.permission = plugin.getName().toLowerCase() + ".use";
    }

    /**
     * Creates a new scoreboard based on the configuration.
     *
     * @param player for who should the scoreboard be set.
     */
    public abstract void createScoreboard(Player player);

    public abstract void createTopListScoreboard(Player player);

    public abstract void onUpdate(Player player);

    public abstract void update(Player player, String variable, int newScore);

    /**
     * Adding all players to the refresh queue and loading the player stats if enabled
     */
    public void registerAll() {
        boolean ispvpstats = Settings.isPvpStats();
        //maybe batch this
        Bukkit.getOnlinePlayers().stream().filter(Player::isOnline).forEach(player -> {
            if (ispvpstats) {
                //maybe batch this
                player.removeMetadata("player_stats", plugin);
                plugin.getStatsDatabase().loadAccountAsync(player);
            }

            plugin.getRefreshTask().addToQueue(player);
        });
    }

    /**
     * Clear the scoreboard for all players
     */
    public void unregisterAll() {
        Bukkit.getOnlinePlayers().forEach(this::unregister);
    }

    /**
     * Unregister ScoreboardStats from the player
     *
     * @param player who owns the scoreboard
     */
    public abstract void unregister(Player player);

    /**
     * Called if the scoreboard should be updated.
     *
     * @param player for who should the scoreboard be set.
     */
    protected abstract void sendUpdate(Player player);

    protected void scheduleShowTask(Player player, boolean action) {
        if (!Settings.isTempScoreboard()) {
            return;
        }

        int interval;
        if (action) {
            interval = Settings.getTempAppear();
        } else {
            interval = Settings.getTempDisappear();
        }

        Bukkit.getScheduler().runTaskLater(plugin, new DelayedShowTask(player, action, this), interval * 20L);
    }

    protected String stripLength(String check) {
        if (check.length() > MAX_ITEM_LENGTH) {
            return check.substring(0, MAX_ITEM_LENGTH);
        }

        return check;
    }

    protected boolean isAllowed(Player player) {
        return player.hasPermission(permission) && Settings.isActiveWorld(player.getWorld().getName());
    }
}
