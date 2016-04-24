package com.github.games647.scoreboardstats;

import com.github.games647.scoreboardstats.config.Settings;
import com.github.games647.scoreboardstats.scoreboard.DelayedShowTask;
import com.github.games647.scoreboardstats.variables.ReplaceManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Manage the scoreboard access.
 */
public abstract class SbManager {

    protected static final String SB_NAME = "Stats";
    protected static final String TEMP_SB_NAME = SB_NAME + 'T';

    private static final int MAX_ITEM_LENGTH = 16;

    protected final ScoreboardStats plugin;
    protected final ReplaceManager replaceManager;

    private final String permission;

    public SbManager(ScoreboardStats plugin) {
        this.plugin = plugin;
        this.replaceManager = new ReplaceManager(this, plugin);

        this.permission = plugin.getName().toLowerCase() + ".use";
    }

    /**
     * Get the replace manager.
     *
     * Warning: The replace manger is bounded to this scoreboard manager.
     * Every time the built-in reload command is executed it will create also
     * a new instance of replace manager.
     *
     * @return the replace manager
     * @deprecated Use ScoreboardStats.getReplaceManager
     */
    public ReplaceManager getReplaceManager() {
        return replaceManager;
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
        for (Player player : BackwardsCompatibleUtil.getOnlinePlayers()) {
            if (player.isOnline()) {
                if (ispvpstats) {
                    //maybe batch this
                    player.removeMetadata("player_stats", plugin);
                    plugin.getStatsDatabase().loadAccountAsync(player);
                }

                plugin.getRefreshTask().addToQueue(player);
            }
        }
    }

    /**
     * Clear the scoreboard for all players
     */
    public void unregisterAll() {
        for (Player player : BackwardsCompatibleUtil.getOnlinePlayers()) {
            unregister(player);
        }
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

        int intervall;
        if (action) {
            intervall = Settings.getTempAppear();
        } else {
            intervall = Settings.getTempDisappear();
        }

        Bukkit.getScheduler().runTaskLater(plugin, new DelayedShowTask(player, action, this), intervall * 20L);
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
