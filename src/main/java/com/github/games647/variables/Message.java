package com.github.games647.variables;

import org.bukkit.ChatColor;

public final class Message  {

    //Log-Messages
    public static final String LOG_NAME              = "[" + Other.PLUGIN_NAME + "] ";
    public static final String NON_EXISTING_DATABASE = "Can't find an existing Database, so creating a new one";
    public static final String LONGER_THAN_LIMIT     = "%s is longer than the limit of 16 characters. This Plugin will cut automatically to the right size.";
    public static final String WRONG_CRAFTBUKKIT     = "The ping feature isn't compatible with your current Craftbukkit version";
    public static final String WRONG_ESSENTIALS      = "Your Essentials version is not compatible with ScoreboardStats please remove the ticks feature";
    public static final String NEW_VERSION           = "A new version is on BukkitDev available";
    public static final String VERSION_CHECK_FAILED  = "The version check failed";
    public static final String SET_SCOREBOARD_FAIL   = "Cannot set the Scoreboard because the player has disconnected";
    public static final String TOO_LONG_LIST         = "One Scoreboard can't have more than 15 items";

    //Player-Messages
    public static final String UPDATE_INFO           = ChatColor.BLUE + "► There is a new update for ScoreboardStats available ◄";
    public static final String RELOAD_SUCCESS        = ChatColor.GREEN + "✔ The configuration was successfully reloaded ✔";
    public static final String PERMISSION_DENIED     = ChatColor.DARK_RED + "✖ You don't have enough permissions to do that ✖";

    //Console
    public static final String NO_CONSOLE            = "This command can't be executed by a console";
}
