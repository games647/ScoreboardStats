package com.github.games647.scoreboardstats.variables;

import com.github.games647.scoreboardstats.variables.defaults.*;
import com.github.games647.scoreboardstats.Lang;
import com.github.games647.scoreboardstats.SbManager;
import com.github.games647.scoreboardstats.Settings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * Handling the replace management
 */
public class ReplaceManager implements Listener {

    private static final Map<Class<? extends VariableReplacer>, String> DEFAULTS;

    static {
        final Map<Class<? extends VariableReplacer>, String> tempMap = Maps.newHashMap();
        tempMap.put(BukkitVariables.class, "ScoreboardStats");
        tempMap.put(GeneralVariables.class, "ScoreboardStats");
        tempMap.put(PlayerPingVariable.class, "ScoreboardStats");
        tempMap.put(StatsVariables.class, "ScoreboardStats");

        tempMap.put(VaultVariables.class, "Vault");

        tempMap.put(HeroesVariables.class, "Heroes");
        tempMap.put(McmmoVariables.class, "mcMMO");
        tempMap.put(SkyblockVariables.class, "uSkyBlock");
//        tempMap.put(PlayerPointsVariables.class, "PlayerPoints");

        tempMap.put(SimpleClansVariables.class, "SimpleClans");
        //factions will be automatically disabled if mcore isn't enabled
        tempMap.put(FactionsVariables.class, "Factions");

        //Prevent further modifications
        DEFAULTS = ImmutableMap.copyOf(tempMap);
    }

    private final Map<VariableReplacer, String> replacers = Maps.newHashMap();
    private final Map<String, VariableReplacer> specificReplacer = Maps.newHashMapWithExpectedSize(15);
    private final List<String> skipList = Lists.newArrayList();

    private final Plugin plugin;
    private final SbManager sbManager;

    /**
     * Creates a new replace manager
     *
     * @param scoreboardManager to manage the scoreboards
     * @param plugin ScoreboardStats plugin
     */
    public ReplaceManager(SbManager scoreboardManager, Plugin plugin) {
        this.plugin = plugin;
        this.sbManager = scoreboardManager;

        Bukkit.getPluginManager().registerEvents(new PluginListener(this), plugin);
        addDefaultReplacer();
    }

    /**
     * Register a new replacer
     *
     * @param replacer the variable replacer
     * @param pluginName the name of the associated plugin
     *
     * @deprecated no longer supported. Will be removed in future versions
     */
    public void register(Replaceable replacer, String pluginName) {
        replacers.put(new LegacyReplaceWrapper(replacer), pluginName);
    }

    /**
     * Register a new replacer
     *
     * @param replacer the variable replacer
     * @param pluginName the name of the associated plugin
     */
    public void register(VariableReplacer replacer, String pluginName) {
        replacers.put(replacer, pluginName);
    }

    /**
     * Unregister a replacer
     *
     * @param replacer the replacer instance
     * @return if the replacer existed
     *
     * @deprecated no longer supported. Will be removed in future versions
     */
    public boolean unregister(Replaceable replacer) {
        return replacers.remove(replacer) != null;
    }

    /**
     * Unregister a replacer
     *
     * @param replacer the replacer instance
     * @return if the replacer existed
     */
    public boolean unregister(VariableReplacer replacer) {
        return replacers.remove(replacer) != null;
    }

    /**
     * Notifies that a scoreboard has changed. This should be called from an
     * event listener.
     *
     * @param player who receives the update
     * @param variable what variable is going to be updated <b>without the variable identifier</b>
     * @param newScore what should be the new score
     */
    public void updateScore(Player player, String variable, int newScore) {
        final String itemName = Settings.getItemName(variable);
        if (itemName != null) {
            sbManager.update(player, itemName, newScore);
        }
    }

    /**
     * Notifies that a scoreboard has changed. This should be called from an
     * event listener. This method will update the variable for all players
     *
     * @param variable what variable is going to be updated
     * @param newScore what should be the new score
     */
    public void updateScore(String variable, int newScore) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            updateScore(onlinePlayer, variable, newScore);
        }
    }

    /**
     * Get the score for a specific variable.
     *
     * @param player the associated player
     * @param variable the variable
     * @param displayName
     * @param oldScore
     * @param complete
     * @return the modified state
     * @throws UnknownVariableException if the variable couldn't be replace
     */
    public ReplaceEvent getScore(Player player, String variable, String displayName, int oldScore, boolean complete) throws UnknownVariableException {
        final ReplaceEvent replaceEvent = new ReplaceEvent(variable, false, false, false, displayName, oldScore);
        if (!complete && skipList.contains(variable)) {
            return replaceEvent;
        }

        //cache found variables
        VariableReplacer replacer = specificReplacer.get(variable);
        if (replacer == null) {
            final Iterator<VariableReplacer> iter = replacers.keySet().iterator();
            while (iter.hasNext()) {
                replacer = iter.next();
                try {
                    replacer.onReplace(player, variable, replaceEvent);
                    if (replaceEvent.isConstant()) {
                        skipList.add(variable);
                    }

                    if (replaceEvent.isModified()) {
                        return replaceEvent;
                    }
                } catch (LinkageError linkageError) {
                    handleReplacerException(replacer, linkageError, iter);
                } catch (Exception exception) {
                    handleReplacerException(replacer, exception, iter);
                }
            }
        } else {
            try {
                replacer.onReplace(player, variable, replaceEvent);
                if (replaceEvent.isConstant()) {
                    skipList.add(variable);
                }

                if (replaceEvent.isModified()) {
                    return replaceEvent;
                }
            } catch (LinkageError linkageError) {
                handleReplacerException(replacer, linkageError, null);
            } catch (Exception exception) {
                handleReplacerException(replacer, exception, null);
            }
        }

        throw new UnknownVariableException("Variable '" + variable + "' not found");
    }

    protected Map<Class<? extends VariableReplacer>, String> getDefaults() {
        return DEFAULTS;
    }

    protected Map<VariableReplacer, String> getReplacers() {
        return replacers;
    }

    protected Map<String, VariableReplacer> getSpecificReplacers() {
        return specificReplacer;
    }

    protected boolean registerDefault(Class<? extends VariableReplacer> replacerClass, String pluginName) {
        try {
            final VariableReplacer instance = createInstance(replacerClass);
            if (!replacers.containsKey(instance)) {
                register(instance, pluginName);
                if (instance instanceof Listener) {
                    Bukkit.getPluginManager()
                            .registerEvents((Listener) instance, Bukkit.getPluginManager().getPlugin(pluginName));
                }
            }

            return true;
        } catch (UnsupportedPluginException ex) {
            plugin.getLogger().warning(Lang.get("unsupportedPluginVersion"
                    , replacerClass.getSimpleName(), ex.getMessage()));
        } catch (Exception ex) {
            //We can't use mulit catches because we need still be compatible with java 6
            plugin.getLogger().log(Level.WARNING, Lang.get("noRegister"), ex);
        } catch (LinkageError linkageError) {
            //only catch these throwables, because they could probably happend
            plugin.getLogger().log(Level.WARNING, Lang.get("noRegister"), linkageError);
        }

        return false;
    }
    private void handleReplacerException(Object source, Throwable toHandle, Iterator<VariableReplacer> removeSource) {
        if (toHandle instanceof LinkageError || toHandle instanceof Exception) {
            //remove the replacer if it throws exceptions, to prevent future ones
            //Maybe we need to catch compiler "errors"
            plugin.getLogger().log(Level.WARNING, Lang.get("replacerException", source), toHandle);
            if (removeSource != null) {
                removeSource.remove();
            }
        }
    }

    //add all replacers that are in the defaults map
    private void addDefaultReplacer() {
        final Set<String> replacersName = Sets.newHashSet();
        for (Map.Entry<Class<? extends VariableReplacer>, String> entry : DEFAULTS.entrySet()) {
            final String pluginName = entry.getValue();
            if (isPluginAvailble(pluginName)) {
                final Class<? extends VariableReplacer> clazz = entry.getKey();
                if (registerDefault(clazz, pluginName)) {
                    //just add it if it was succesfull
                    replacersName.add(clazz.getSimpleName());
                }
            }
        }

        //log registered replacers
        plugin.getLogger().log(Level.INFO, "Registered replacers: {0}", replacersName);
    }

    //Check if specific plugin is availble and activated
    private boolean isPluginAvailble(String pluginName) {
        return Bukkit.getPluginManager().isPluginEnabled(pluginName);
    }

    private VariableReplacer createInstance(Class<? extends VariableReplacer> replacerClass)
            throws InstantiationException, InvocationTargetException, IllegalAccessException {
        try {
            return replacerClass.getConstructor(ReplaceManager.class).newInstance(this);
        } catch (NoSuchMethodException ex) {
            return replacerClass.newInstance();
        }
    }
}
