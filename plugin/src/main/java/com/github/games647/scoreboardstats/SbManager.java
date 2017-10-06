package com.github.games647.scoreboardstats;

import com.github.games647.BoardManager;
import com.github.games647.scoreboardstats.config.Settings;
import com.github.games647.scoreboardstats.variables.VariableItem;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Manage the scoreboard access.
 */
public abstract class SbManager implements BoardManager {

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

    @Override
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

    @Override
    public void unregisterAll() {
        Bukkit.getOnlinePlayers().forEach(this::unregister);
    }

    @Override
    public void updateByVariable(Player player, String variable, int newScore) {
        VariableItem variableItem = Settings.getMainScoreboard().getItemsByVariable().get(variable);
        if (variableItem != null) {
            update(player, variableItem.getDisplayText(), newScore);
        }
    }

    protected abstract void update(Player player, String title, int newScore);

    protected void scheduleShowTask(Player player, boolean action) {
        if (!Settings.isTempScoreboard()) {
            return;
        }

        int interval = Settings.getTempDisappear();
        if (action) {
            interval = Settings.getTempAppear();
        }

        UUID uuid = player.getUniqueId();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Player localPlayer = Bukkit.getPlayer(uuid);
            if (localPlayer == null) {
                return;
            }

            if (action) {
                createTopListScoreboard(player);
            } else {
                createScoreboard(player);
            }
        }, interval * 20L);
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
