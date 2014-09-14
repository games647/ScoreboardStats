package com.github.games647.scoreboardstats;

import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
import com.github.games647.scoreboardstats.variables.UnknownVariableException;

import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * Managing the scoreboard access.
 *
 * @see com.github.games647.scoreboardstats.protocol.PacketSbManager
 */
public class SbManager {

    protected static final String SB_NAME = "Stats";
    protected static final String TEMP_SB_NAME = SB_NAME + 'T';

    private static final String DUMMY_CRITERIA = "dummy";

    protected final ScoreboardStats plugin;

    protected final ReplaceManager replaceManager;

    private final boolean oldBukkit;

    /**
     * Initialize scoreboard manager.
     *
     * @param pluginInstance the ScoreboardStats instance
     */
    public SbManager(ScoreboardStats pluginInstance) {
        this.plugin = pluginInstance;
        replaceManager = new ReplaceManager(pluginInstance);

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
        if (!isValid(player) || oldObjective != null && !TEMP_SB_NAME.equals(oldObjective.getName())) {
            //Check if another scoreboard is showing
            return;
        }

        //Creates a new scoreboard and a new objective
        final Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        final Objective objective = board.registerNewObjective(SB_NAME, DUMMY_CRITERIA);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(Settings.getTitle());

        try {
            player.setScoreboard(board);
        } catch (IllegalStateException stateEx) {
            //the player logged out - fail silently
            return;
        }

        sendUpdate(player, true);
        //Schedule the next tempscoreboard show
        if (Settings.isTempScoreboard()) {
            Bukkit.getScheduler().runTaskLater(plugin
                    , new ShowTask(player, true), Settings.getTempAppear() * 20L);
        }
    }

    /**
     * Adding all players to the refresh queue and loading the player stats if enabled
     */
    public void registerAll() {
        final boolean ispvpstats = Settings.isPvpStats();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOnline()) {
                if (ispvpstats) {
                    //maybe batch this
                    Database.loadAccountAsync(player);
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
        if (!isValid(player) || oldObjective == null
                || !oldObjective.getName().startsWith(SB_NAME)) {
            //Check if another scoreboard is showing
            return;
        }

        //remove old scores
        if (TEMP_SB_NAME.equals(oldObjective.getName())) {
            oldObjective.unregister();
        }

        final Scoreboard board = player.getScoreboard();
        final Objective objective = board.registerNewObjective(TEMP_SB_NAME, DUMMY_CRITERIA);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(Settings.getTempTitle());

        try {
            player.setScoreboard(board);
        } catch (IllegalStateException stateEx) {
            //the player logged out - fail silently
            return;
        }

        //Colorize and send all elements
        for (Map.Entry<String, Integer> entry : Database.getTop()) {
            final String color = Settings.getTempColor();
            final String scoreName = stripLength(String.format("%s%s", color, entry.getKey()));
            sendScore(objective, scoreName, entry.getValue(), true);
        }
        //schedule the next normal scoreboard show
        Bukkit.getScheduler().runTaskLater(plugin
                , new ShowTask(player, false), Settings.getTempDisappear() * 20L);
    }

    protected boolean isValid(Player player) {
        return player.hasPermission("scoreboardstats.use") && player.isOnline()
                && Settings.isActiveWorld(player.getWorld().getName());
    }

    protected String stripLength(String check) {
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
                }
            }
        }
    }

    private void sendScore(Objective objective, String title, int value, boolean complete) {
        String scoreName = ChatColor.translateAlternateColorCodes('&', title);
        final int titleLength = scoreName.length();
        if (titleLength > 16) {
            final Scoreboard scoreboard = objective.getScoreboard();
            //Could maybe cause conflicts but .substring could also make conflicts if they have the same beginning
            final String teamId = String.valueOf(scoreName.hashCode());

            Team team = scoreboard.getTeam(teamId);
            if (team == null) {
                team = scoreboard.registerNewTeam(teamId);
                team.setPrefix(scoreName.substring(0, 16));
                if (titleLength > 32) {
                    //we already validated that this one can only be 48 characters long in the Settings class
                    team.setSuffix(scoreName.substring(32));
                    scoreName = scoreName.substring(16, 32);
                } else {
                    scoreName = scoreName.substring(16);
                }

                team.setDisplayName(scoreName);
                //Bukkit.getOfflinePlayer performance work around
                team.addPlayer(new FastOfflinePlayer(scoreName));
            } else {
                scoreName = team.getDisplayName();
            }
        }

        Score score;
        if (oldBukkit) {
            //Bukkit.getOfflinePlayer performance work around
            score = objective.getScore(new FastOfflinePlayer(scoreName));
        } else {
            score = objective.getScore(scoreName);
        }

        if (complete && value == 0) {
            /*
             * Workaround because the value from Bukkit is set as default to zero and Bukkit sends only
             * the packet if the value changes
             * so we have to change it to another value earlier
             */
            score.setScore(1337);
        }

        if (score.getScore() != value) {
            //don't spam the client
            score.setScore(value);
        }
    }

    private boolean isOldBukkit() {
        final int compare = Version.compare("1.7.8", Version.getMinecraftVersionString());
        if (compare >= 0) {
            try {
                Objective.class.getDeclaredMethod("getScore", String.class);
                //We have access to the new method
                return false;
            } catch (NoSuchMethodException noSuchMethodEx) {
                //since we have an extra class for it (FastOfflinePlayer)
                //we can fail silently
            }
        }

        //The version is under 1.7.8 so the method doesn't exist
        return true;
    }

    /**
     * Scheduled appear task
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
