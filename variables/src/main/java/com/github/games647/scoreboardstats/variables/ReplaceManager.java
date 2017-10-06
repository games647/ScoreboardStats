package com.github.games647.scoreboardstats.variables;

import com.github.games647.BoardManager;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;

/**
 * Handling the replace management
 */
public class ReplaceManager implements Listener {

    public static final String UNSUPPORTED_VERSION = "The Replacer: {} cannot be registered - the plugin version isn't supported \n\t({})";
    public static final String REPLACER_EXCEPTION = "Replacer occurred an error: {} for {} So it will be removed to prevent future errors";

    private final Set<String> skipList = Sets.newHashSet();
    private final Set<VariableReplaceAdapter<?>> legacyReplacers = Sets.newHashSet();
    private final Map<String, VariableReplaceAdapter<?>> globals = Maps.newHashMap();
    private final Map<String, VariableReplaceAdapter<?>> specificReplacer = Maps.newHashMap();

    private final Plugin plugin;
    private final BoardManager sbManager;

    private final Logger logger;

    /**
     * Creates a new replace manager
     *
     * @param scoreboardManager to manage the scoreboards
     * @param plugin            ScoreboardStats plugin
     */
    public ReplaceManager(BoardManager scoreboardManager, Plugin plugin, Logger logger) {
        this.plugin = plugin;
        this.sbManager = scoreboardManager;
        this.logger = logger;

        Bukkit.getPluginManager().registerEvents(new PluginListener(this), plugin);
        addDefaultReplacers();
    }

    /**
     * Register a new replacer
     *
     * @param replacer  the variable replacer
     * @param plugin    the associated plugin
     * @param variables all variables which this replacer can replace <b>without the variable identifiers (%)</b>
     */
    public void register(VariableReplacer replacer, Plugin plugin, String... variables) {
        register(new ReplaceWrapper(replacer, plugin, variables));
    }

    /**
     * Register a new replacer
     *
     * @param replacer    the variable replacer
     * @param global      is the value the same for all players or does the replacer needs a specific player
     * @param async       is this plugin thread safe
     * @param constant    if the variable is updated based on events
     * @param description description of all variables of this plugin
     * @param plugin      associated plugin instance
     * @param variables   to replaced variables <b>without the variable identifiers (%)</b>
     */
    public void register(VariableReplacer replacer, Plugin plugin
            , String description, boolean global, boolean async, boolean constant, String... variables) {
        register(new ReplaceWrapper(replacer, plugin, description, global, async, constant, variables));
    }

    /**
     * Register a new replacer
     *
     * @param replacer the variable replacer
     */
    public void register(VariableReplaceAdapter<? extends Plugin> replacer) {
        for (String variable : replacer.getVariables()) {
            if (variable.contains("*")) {
                //contains wildcard
                legacyReplacers.add(replacer);
            }

            specificReplacer.put(variable, replacer);
            if (replacer.isConstant() || replacer.isGlobal()) {
                skipList.add(variable);
                if (replacer.isGlobal() && !replacer.isConstant()) {
                    //if constant we don't need to update it manually
                    globals.put(variable, replacer);
                }
            }
        }
    }

    /**
     * Unregister a replacer
     *
     * @param replacer the replacer instance
     * @return if the replacer existed
     */
    public boolean unregister(VariableReplacer replacer) {
        boolean found = false;

        Iterator<VariableReplaceAdapter<?>> iterator = specificReplacer.values().iterator();
        while (iterator.hasNext()) {
            VariableReplaceAdapter<?> next = iterator.next();
            if (next.equals(replacer)) {
                iterator.remove();
                found = true;
            }
        }

        return found;
    }

    /**
     * Notifies that a scoreboard value has changed. This should be called from an event listener.
     *
     * @param player   who receives the update
     * @param variable what variable is going to be updated <b>without the variable identifier</b>
     * @param newScore what should be the new score
     */
    public void updateScore(Player player, String variable, int newScore) {
        sbManager.updateByVariable(player, variable, newScore);
    }

    /**
     * Notifies that a scoreboard value has changed. This should be called from an event listener. This method will
     * update the variable for all players
     *
     * @param variable what variable is going to be updated
     * @param newScore what should be the new score
     */
    public void updateScore(String variable, int newScore) {
        Bukkit.getOnlinePlayers().forEach(pl -> updateScore(pl, variable, newScore));
    }

    /**
     * Get the score for a specific variable.
     *
     * @param player      the associated player
     * @param variable    the variable
     * @param displayName the display name of the scoreboard item
     * @param oldScore    the score of the scoreboard item
     * @param complete    whether it's the first refresh
     * @return the modified state
     * @throws UnknownVariableException if the variable couldn't be replace
     */
    public ReplaceEvent getScore(Player player, String variable, String displayName, int oldScore, boolean complete)
            throws UnknownVariableException {
        ReplaceEvent replaceEvent = new ReplaceEvent(variable, false, displayName, oldScore);
        if (!complete && skipList.contains(variable)) {
            //Check if the variable can be updated with event handlers or is global
            //therefore we just need a initial value
            return replaceEvent;
        }

        //cache found variables
        VariableReplacer replacer = specificReplacer.get(variable);
        if (replacer == null) {
            getScoreLegacy(player, variable, replaceEvent);
        } else {
            try {
                replacer.onReplace(player, variable, replaceEvent);
            } catch (LinkageError | Exception replacerException) {
                //remove the replacer if it throws exceptions, to prevent future ones
                //Maybe we need to catch compiler "errors"
                logger.warn(REPLACER_EXCEPTION, replacerException, replacer);
                unregister(replacer);
            }
        }

        if (complete && replaceEvent.isConstant()) {
            skipList.add(variable);
            //they are updated with events so we don't need to update it manually
            globals.remove(variable);
        }

        return replaceEvent;
    }


    public void updateGlobals() {
        for (Map.Entry<String, VariableReplaceAdapter<?>> entrySet : globals.entrySet()) {
            String variable = entrySet.getKey();
            ReplaceEvent replaceEvent = new ReplaceEvent(variable, false, variable, -1);

            VariableReplaceAdapter<? extends Plugin> globalReplacer = entrySet.getValue();
            globalReplacer.onReplace(null, variable, replaceEvent);
            if (replaceEvent.isModified()) {
                updateScore(variable, replaceEvent.getScore());
            }
        }
    }

    protected Map<String, VariableReplaceAdapter<? extends Plugin>> getReplacers() {
        return specificReplacer;
    }

    private void getScoreLegacy(Player player, String variable, ReplaceEvent replaceEvent)
            throws UnknownVariableException {
        for (Iterator<VariableReplaceAdapter<?>> iterator = legacyReplacers.iterator(); iterator.hasNext(); ) {
            VariableReplaceAdapter<?> legacyReplacer = iterator.next();

            try {
                legacyReplacer.onReplace(player, variable, replaceEvent);
            } catch (LinkageError | Exception replacerException) {
                logger.warn(REPLACER_EXCEPTION, replacerException, legacyReplacer);
                iterator.remove();
            }

            if (replaceEvent.isModified()) {
                specificReplacer.put(variable, legacyReplacer);
                //fast return
                return;
            }
        }

        if (!replaceEvent.isModified()) {
            throw new UnknownVariableException("Variable '" + variable + "' not found");
        }
    }

    protected boolean registerDefault(Class<? extends VariableReplaceAdapter<?>> replacerClass) {
        try {
            VariableReplaceAdapter<?> instance = createInstance(replacerClass);
            register(instance);
            if (instance instanceof Listener) {
                Bukkit.getPluginManager().registerEvents((Listener) instance, instance.getPlugin());
            }

            return true;
        } catch (IllegalArgumentException argumentEx) {
            //ignore plugin class cannot be found
        } catch (UnsupportedPluginException ex) {
            logger.warn(UNSUPPORTED_VERSION, replacerClass.getSimpleName(), ex.getMessage());
        } catch (Exception | LinkageError replacerException) {
            //only catch these throwables, because they could probably happened
            logger.warn("Cannot register replacer", replacerException);
        }

        return false;
    }

    //add all replacers that are in the defaults map
    private void addDefaultReplacers() {
        Set<String> replacersName = Sets.newHashSet();
        try {
            ClassPath path = ClassPath.from(getClass().getClassLoader());

            for (ClassInfo classInfo : path.getTopLevelClasses("com.github.games647.scoreboardstats.default")) {
                Class<?> clazz = classInfo.load();
                if (clazz.isAssignableFrom(VariableReplaceAdapter.class)) {
                    if (registerDefault((Class<? extends VariableReplaceAdapter<?>>) clazz)) {
                        //just add it if it was successful
                        replacersName.add(clazz.getSimpleName());
                    }
                }
            }
        } catch (IOException ioEx) {
            logger.error("Failed to register replayers", ioEx);
        }

        logger.info("Registered replacers: {}", replacersName);
    }

    private VariableReplaceAdapter<?> createInstance(Class<? extends VariableReplaceAdapter<?>> replacerClass)
            throws InstantiationException, InvocationTargetException, IllegalAccessException {
        try {
            return replacerClass.getConstructor(ReplaceManager.class).newInstance(this);
        } catch (NoSuchMethodException ex) {
            return replacerClass.newInstance();
        }
    }
}
