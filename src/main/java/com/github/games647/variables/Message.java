package com.github.games647.variables;

public final class Message  {

    public static final String BLUE     = Other.TRANSLATED_CHAR + "9";
    public static final String AQUA     = Other.TRANSLATED_CHAR + "b";
    public static final String GREEN    = Other.TRANSLATED_CHAR + "a";
    public static final String DARK_RED = Other.TRANSLATED_CHAR + "4";
    public static final String RED      = Other.TRANSLATED_CHAR + "c";
    public static final String YELLOW   = Other.TRANSLATED_CHAR + "6";

    //Log-Messages
    public static final String LOG_NAME              = '[' + Other.PLUGIN_NAME + "] ";
    public static final String NON_EXISTING_DATABASE = YELLOW   + "Can't find an existing Database, so creating a new one";
    public static final String LONGER_THAN_LIMIT     = RED      + "%s was longer than the limit of %s characters. This Plugin will cut automatically to the right size.";
    public static final String SET_SCOREBOARD_FAIL   = YELLOW   + "Cannot set the Scoreboard because the player has disconnected";
    public static final String TOO_LONG_LIST         = RED      + "One Scoreboard can't have more than 15 items";

    //Player-Messages
    public static final String RELOAD_SUCCESS        = GREEN    + "✔ The configuration was successfully reloaded ✔";
    public static final String PERMISSION_DENIED     = DARK_RED + "✖ You don't have enough permissions to do that ✖";

    //Console
    public static final String NO_CONSOLE            = "This command can't be executed by a console";
}
