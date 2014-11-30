package com.github.games647.scoreboardstats.variables;

import com.github.games647.scoreboardstats.Lang;
import com.github.games647.scoreboardstats.ScoreboardStats;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Handling the replace management
 */
public final class ReplaceManager implements Listener {

    private static final Map<Class<? extends Replaceable>, String> DEFAULTS;

    static {
        final Map<Class<? extends Replaceable>, String> tempMap = Maps.newHashMap();
        tempMap.put(GeneralVariables.class, "ScoreboardStats");
        tempMap.put(PlayerPingVariable.class, "ScoreboardStats");
        tempMap.put(StatsVariables.class, "ScoreboardStats");

        tempMap.put(VaultVariables.class, "Vault");

        tempMap.put(HeroesVariables.class, "Heroes");
        tempMap.put(McmmoVariables.class, "mcMMO");

        tempMap.put(SimpleClansVariables.class, "SimpleClans");
        //factions will be automatically disabled if mcore isn't enabled
        tempMap.put(FactionsVariables.class, "Factions");

        //Prevent further modifications
        DEFAULTS = ImmutableMap.copyOf(tempMap);
    }

    private final Map<Replaceable, String> replacers = Maps.newHashMap();
    private final ScoreboardStats plugin;

    /**
     * Creates a new replace manager
     *
     * @param plugin ScoreboardStats plugin
     */
    public ReplaceManager(ScoreboardStats plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
        addDefaultReplacer();
    }

    /**
     * Register a new replacer
     *
     * @param replacer the variable replacer
     * @param pluginName the name of the associated plugin
     *
     * @throws NullPointerException if replacer is null
     * @throws NullPointerException if pluginName is null
     * @throws IllegalArgumentException if plugin isn't available or activated
     * @throws IllegalArgumentException if pluginName is empty
     * @throws IllegalStateException if replacer is already registered
     */
    public void register(Replaceable replacer, String pluginName) {
        //Check all arguments and inform the developer if there something wrong
        Preconditions.checkNotNull(replacer, "replacer cannot be null");
        Preconditions.checkNotNull(pluginName, "pluginName cannot be null");

        Preconditions.checkArgument(!pluginName.isEmpty(), "the pluginName cannot be empty");
        Preconditions.checkArgument(isPluginAvailble(pluginName), "this plugin isn't available or activated");

        Preconditions.checkState(!replacers.containsKey(replacer), "this replacer is already registered");

        replacers.put(replacer, pluginName);
    }

    /**
     * Unregister a replacer
     *
     * @param replacer the replacer instance
     * @return if the replacer existed
     */
    public boolean unregister(Replaceable replacer) {
        return replacers.remove(replacer) != null;
    }

    /**
     * Get the score for a specific variable.
     *
     * @param player the associated player
     * @param variable the variable
     * @return the score
     * @throws UnknownVariableException if the variable couldn't be replace
     */
    public int getScore(Player player, String variable) throws UnknownVariableException {
        final Iterator<Replaceable> iter = replacers.keySet().iterator();
        while (iter.hasNext()) {
            final Replaceable replacer = iter.next();
            try {
                final int scoreValue = replacer.getScoreValue(player, variable);
                if (Replaceable.UNKOWN_VARIABLE != scoreValue) {
                    return scoreValue;
                }
            } catch (Exception ex) {
                //remove the replacer if it throws exceptions, to prevent future ones
                iter.remove();

                plugin.getLogger().log(Level.WARNING,
                        Lang.get("replacerException", replacer), ex);
            } catch (NoClassDefFoundError noClassEr) {
                //only catch these throwable, because they could probably happend
                iter.remove();

                plugin.getLogger()
                        .log(Level.WARNING, Lang.get("replacerException", replacer), noClassEr);
            } catch (NoSuchMethodError noSuchMethodEr) {
                iter.remove();

                plugin.getLogger()
                        .log(Level.WARNING, Lang.get("replacerException", replacer), noSuchMethodEr);
            }
        }

        throw new UnknownVariableException("Variable '" + variable + "' not found");
    }

    /**
     * Check for disabled plugin to re add the associated replacer
     *
     * @param enableEvent the enable event
     */
    @EventHandler
    public void onPluginEnable(PluginEnableEvent enableEvent) {
        //Register the listener back again if the plugin is availble
        final String enablePluginName = enableEvent.getPlugin().getName();
        for (Map.Entry<Class<? extends Replaceable>, String> entry: DEFAULTS.entrySet()) {
            final String pluginName = entry.getValue();
            if (enablePluginName.equals(entry.getValue())) {
                registerDefault(entry.getKey(), pluginName);
            }
        }
    }

    /**
     * Check for disabled plugin to remove the associated replacer
     *
     * @param disableEvent the disable event
     */
    @EventHandler
    public void onPluginDisable(PluginDisableEvent disableEvent) {
        //Remove the listener if the associated plugin was disabled
        final String disablePluginName = disableEvent.getPlugin().getName();

        final Iterator<String> iter = replacers.values().iterator();
        while (iter.hasNext()) {
            final String entry = iter.next();
            if (disablePluginName.equals(entry)) {
                iter.remove();
            }
        }
    }

    //add all replacers that are in the defaults map
    private void addDefaultReplacer() {
        final Set<String> replacersName = Sets.newHashSet();
        for (Map.Entry<Class<? extends Replaceable>, String> entry : DEFAULTS.entrySet()) {
            final String pluginName = entry.getValue();
            if (isPluginAvailble(pluginName)) {
                final Class<? extends Replaceable> clazz = entry.getKey();
                registerDefault(clazz, pluginName);
                replacersName.add(clazz.getSimpleName());
            }
        }

        //log registered replacers
        plugin.getLogger().log(Level.INFO, "Registered replacers: {0}", replacersName);
    }

    //Check if specific plugin is availble and activated
    private boolean isPluginAvailble(String pluginName) {
        return Bukkit.getPluginManager().isPluginEnabled(pluginName);
    }

    private void registerDefault(Class<? extends Replaceable> replacerClass, String pluginName) {
        try {
            final Replaceable instance = replacerClass.newInstance();
            if (!replacers.containsKey(instance)) {
                register(instance, pluginName);
            }
        } catch (UnsupportedPluginException ex) {
            plugin.getLogger().warning(Lang.get("unsupportedPluginVersion"
                    , replacerClass.getSimpleName(), ex.getMessage()));
        } catch (Exception ex) {
            //We can't use mulit catches because we need still be compatible with java 6
            plugin.getLogger().log(Level.WARNING, Lang.get("noRegister"), ex);
        } catch (NoClassDefFoundError noClassEr) {
            //only catch these throwables, because they could probably happend
           plugin.getLogger().log(Level.WARNING, Lang.get("noRegister"), noClassEr);
        } catch (NoSuchMethodError noSuchMethodEr) {
            plugin.getLogger().log(Level.WARNING, Lang.get("noRegister"), noSuchMethodEr);
        }
    }
}

