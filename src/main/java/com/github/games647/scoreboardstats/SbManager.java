package com.github.games647.scoreboardstats;

import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
import com.github.games647.scoreboardstats.variables.UnknownVariableException;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Managing the scoreboard access.
 */
public class SbManager {

    protected static final String SB_NAME = "Stats";
    protected static final String TEMP_SB_NAME = SB_NAME + 'T';

    protected final ScoreboardStats plugin;

    protected final ReplaceManager replaceManager = new ReplaceManager();

    private final boolean oldBukkit;

    /**
     *
     * @param pluginInstance the scoreboardstats instance
     */
    public SbManager(ScoreboardStats pluginInstance) {
        this.plugin = pluginInstance;

        oldBukkit = isOldBukkit();
    }

    /**
     * Get the replace manager.
     *
     * @return the replace manager
     */
    public ReplaceManager getReplaceManager() {
        return replaceManager;
    }

    /**
     * Creates a new scoreboard based on the configuration.
     *
     * @param player for who should the scoreboard be set.
     */
    public void createScoreboard(Player player) {
        final Objective oldObjective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        if (!isPermitted(player)
                //Check if another scoreboard is showing
                || oldObjective != null
                && !TEMP_SB_NAME.equals(oldObjective.getName())) {
            return;
        }

        //Creates a new scoreboard and a new objective
        final Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        final Objective objective = board.registerNewObjective(SB_NAME, "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(Settings.getTitle());

        boolean success = false;
        try {
            player.setScoreboard(board);
            success = true;
        } catch (IllegalStateException stateEx) {
            //fail silently
        }

        if (success) {
            sendUpdate(player, true);
            //Schedule the next tempscoreboard show
            if (Settings.isTempScoreboard()) {
                Bukkit.getScheduler().runTaskLater(plugin
                        , new ShowTask(player, true), Settings.getTempAppear() * 20L);
            }
        }
    }

    /**
     * Adding all players to the refresh queue and loading the player stats if enabled
     */
    public void registerAll() {
        final boolean ispvpstats = Settings.isPvpStats();
        for (Player player: Bukkit.getOnlinePlayers()) {
            if (player.isOnline()) {
                if (ispvpstats) {
                    Database.loadAccount(player);
                }

                plugin.getRefreshTask().addToQueue(player);
            }
        }
    }

    /**
     * Clear the scoreboard for all players
     */
    public void unregisterAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOnline()) {
                final Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
                if (objective != null && objective.getName().startsWith(SB_NAME)) {
                    objective.unregister();
                }
            }
        }
    }

    /**
     * Called if the scoreboard should be updated.
     *
     * @param player for who should the scoreboard be set.
     */
    public void sendUpdate(Player player) {
        final Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        if (objective == null) {
            //The player has no scoreboard so create one
            createScoreboard(player);
        } else {
            sendUpdate(player, false);
        }
    }

    protected void createTopListScoreboard(Player player) {
        final Objective oldObjective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        if (!isPermitted(player) || oldObjective == null
                || !oldObjective.getName().startsWith(SB_NAME)) {
            //Check if another scoreboard is showing
            return;
        }

        //remove old scores
        if (TEMP_SB_NAME.equals(oldObjective.getName())) {
            oldObjective.unregister();
        }

        final Scoreboard board = player.getScoreboard();
        final Objective objective = board.registerNewObjective(TEMP_SB_NAME, "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(Settings.getTempTitle());

        boolean success = false;
        try {
            player.setScoreboard(board);
            success = true;
        } catch (IllegalStateException stateEx) {
            //fail silently
        }

        if (success) {
            //Colorize and send all elements
            for (Map.Entry<String, Integer> entry : Database.getTop()) {
                final String color = Settings.getTempColor();
                final String scoreName = checkLength(String.format("%s%s", color, entry.getKey()));
                sendScore(objective, scoreName, entry.getValue(), true);
            }
            //schedule the next normal scoreboard show
            Bukkit.getScheduler().runTaskLater(plugin
                    , new ShowTask(player, false), Settings.getTempDisappear() * 20L);
        }
    }

    protected boolean isPermitted(Player player) {
        return player.hasPermission("scoreboardstats.use");
    }

    protected String checkLength(String check) {
        if (check.length() > 16) {
            return check.substring(0, 16);
        }

        return check;
    }

    private void sendUpdate(Player player, boolean complete) {
        final Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        if (SB_NAME.equals(objective.getName())) {
            final Iterator<Map.Entry<String, String>> iter = Settings.getItems();
            while (iter.hasNext()) {
                final Map.Entry<String, String> entry = iter.next();
                final String title = entry.getKey();
                final String variable = entry.getValue();
                try {
                    final int score = replaceManager.getScore(player, variable);
                    sendScore(objective, title, score, complete);
                } catch (UnknownVariableException ex) {
                    //Remove the variable becaue we can't replace it
                    iter.remove();

                    plugin.getLogger().info(Lang.get("unknownVariable", variable));
                    plugin.getLogger().log(Level.FINE, null, ex);
                }
            }
        }
    }

    private void sendScore(Objective objective, String title, int value, boolean complete) {
        final String name = ChatColor.translateAlternateColorCodes('&', title);

        final Score score;
        if (oldBukkit) {
            //This could affect the server performance in 1.7.8
            score = objective.getScore(Bukkit.getOfflinePlayer(name));
        } else {
            score = objective.getScore(name);
        }

        if (complete && value == 0) {
            /*
             * Workaround because the value from bukkit is set as default to zero and bukkit sends only
             * the packet if the value changes
             * so we have to change it to another value earlier
             */
            score.setScore(1337);
        }

        score.setScore(value);
    }

    private boolean isOldBukkit() {
        final int compare = ReplaceManager.compare("1.7.8", plugin.getMinecraftVersion());
        if (compare >= 0) {
            try {
                Objective.class.getDeclaredMethod("getScore", String.class);
            } catch (NoSuchMethodException noSuchMethodEx) {
                plugin.getLogger().warning("You have an old version of your server. "
                        + "This version old version affects the performance in a negative way. "
                        + "Please update your server to a newer build");
                return true;
            }

            //We have access to the new method
            return false;
        }

        //The version is under 1.7.8 so the method doesn't exist
        return true;
    }

    /**
     * Scheduled appaer task
     */
    public class ShowTask implements Runnable {

        private final Player player;
        private final boolean action;

        /**
         * Creates a new scheduled appear of the normal scoreboard or the temp scoreboard
         *
         * @param player the specific player
         * @param action if the temp scoreboard be displayed
         */
        public ShowTask(Player player, boolean action) {
            this.player = player;
            this.action = action;
        }

        @Override
        public void run() {
            if (action) {
                createTopListScoreboard(player);
            } else {
                createScoreboard(player);
            }
        }
    }
}
