package com.github.games647.scoreboardstats.variables;

import com.github.games647.scoreboardstats.variables.defaults.*;
import com.github.games647.scoreboardstats.config.Lang;
import com.github.games647.scoreboardstats.SbManager;
import com.github.games647.scoreboardstats.config.Settings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
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

    private static final Map<Class<? extends VariableReplaceAdapter<?>>, String> DEFAULTS;

    static {
        final Map<Class<? extends VariableReplaceAdapter<?>>, String> tempMap = Maps.newHashMap();
        //empty value means this plugin
        tempMap.put(BukkitVariables.class, "");
        tempMap.put(BukkitGlobalVariables.class, "");
        tempMap.put(GeneralVariables.class, "");
        tempMap.put(PlayerPingVariable.class, "");

        tempMap.put(VaultVariables.class, "Vault");

        tempMap.put(HeroesVariables.class, "Heroes");
        tempMap.put(McmmoVariables.class, "mcMMO");
        tempMap.put(SkyblockVariables.class, "uSkyBlock");
        tempMap.put(PlayerPointsVariables.class, "PlayerPoints");
        tempMap.put(McPrisonVariables.class, "Prison");
        tempMap.put(BukkitGamesVariables.class, "BukkitGames");
        tempMap.put(CraftconomyVariables.class, "Craftconomy3");

        tempMap.put(SimpleClansVariables.class, "SimpleClans");
        //factions will be automatically disabled if mcore isn't enabled
        tempMap.put(FactionsVariables.class, "Factions");

        //Prevent further modifications
        DEFAULTS = ImmutableMap.copyOf(tempMap);
    }

    private final Set<String> skipList = Sets.newHashSet();
    private final Set<VariableReplaceAdapter<?>> legacyReplacers = Sets.newHashSet();
    private final Map<String, VariableReplaceAdapter<?>> globals = Maps.newHashMap();
    private final Map<String, VariableReplaceAdapter<?>> specificReplacer = Maps.newHashMap();

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
        addDefaultReplacers();
    }

    /**
     * @param replacer the variable replacer
     * @param pluginName the name of the associated plugin
     *
     * @deprecated no longer supported. Will be removed in future versions
     */
    @Deprecated
    public void register(Replaceable replacer, String pluginName) {
        legacyReplacers.add(new LegacyReplaceWrapper(Bukkit.getPluginManager().getPlugin(pluginName), replacer));
    }

    /**
     * Register a new replacer
     *
     * @param replacer the variable replacer
     * @param plugin the associated plugin
     * @param variables all variables which this replacer can replace <b>without the variable identifiers (%)</b>
     */
    public void register(VariableReplacer replacer, Plugin plugin, String... variables) {
        register(new ReplaceWrapper(replacer, plugin, variables));
    }

    /**
     * Register a new replacer
     *
     * @param replacer the variable replacer
     * @param global is the value the same for all players or does the replacer needs a specific player
     * @param async is this plugin thread safe
     * @param constant if the variable is updated based on events
     * @param description description of all variables of this plugin
     * @param plugin associated plugin instance
     * @param variables to replaced variables <b>without the variable identifiers (%)</b>
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
     * @param replacer the replacer instance
     * @return if the replacer existed
     *
     * @deprecated no longer supported. Will be removed in future versions
     */
    @Deprecated
    public boolean unregister(Replaceable replacer) {
        boolean found = false;

        final Iterator<VariableReplaceAdapter<?>> iterator = specificReplacer.values().iterator();
        while (iterator.hasNext()) {
            final VariableReplaceAdapter<?> next = iterator.next();
            if (next.equals(replacer)) {
                iterator.remove();
                found = true;
            }
        }

        if (legacyReplacers.remove(replacer)) {
            found = true;
        }

        return found;
    }

    /**
     * Unregister a replacer
     *
     * @param replacer the replacer instance
     * @return if the replacer existed
     */
    public boolean unregister(VariableReplacer replacer) {
        boolean found = false;

        final Iterator<VariableReplaceAdapter<?>> iterator = specificReplacer.values().iterator();
        while (iterator.hasNext()) {
            final VariableReplaceAdapter<?> next = iterator.next();
            if (next.equals(replacer)) {
                iterator.remove();
                found = true;
            }
        }

        return found;
    }

    /**
     * Notifies that a scoreboard value has changed. This should be called from an
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
     * Notifies that a scoreboard  value has changed. This should be called from an
     * event listener. This method will update the variable for all players
     *
     * @param variable what variable is going to be updated
     * @param newScore what should be the new score
     */
    public void updateScore(String variable, int newScore) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateScore(player, variable, newScore);
        }
    }

//    public void updateVariableLater(String variable) {
//
//    }
//
//    public void updateVariableLater(Player player, String variable) {
//
//    }

    /**
     * Get the score for a specific variable.
     *
     * @param player the associated player
     * @param variable the variable
     * @param displayName the display name of the scoreboard item
     * @param oldScore the score of the scoreboard item
     * @param complete whether it's the first refresh
     * @return the modified state
     * @throws UnknownVariableException if the variable couldn't be replace
     */
    public ReplaceEvent getScore(Player player, String variable, String displayName, int oldScore, boolean complete)
            throws UnknownVariableException {
        final ReplaceEvent replaceEvent = new ReplaceEvent(variable, false, false, displayName, oldScore);
        if (!complete && skipList.contains(variable)) {
            //Check if the variable can be updated with event handlers or is global
            //therefore we just need a initial value
            return replaceEvent;
        }

        //cache found variables
        final VariableReplacer replacer = specificReplacer.get(variable);
        if (replacer == null) {
            getScoreLegacy(player, variable, replaceEvent);
        } else {
            try {
                replacer.onReplace(player, variable, replaceEvent);
            } catch (LinkageError linkageError) {
                //remove the replacer if it throws exceptions, to prevent future ones
                //Maybe we need to catch compiler "errors"
                plugin.getLogger().log(Level.WARNING, Lang.get("replacerException", replacer), linkageError);
                unregister(replacer);
            } catch (Exception exception) {
                //remove the replacer if it throws exceptions, to prevent future ones
                plugin.getLogger().log(Level.WARNING, Lang.get("replacerException", replacer), exception);
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

    /**
     * Executes an update on all global replacers
     */
    public void updateGlobals() {
        for (Map.Entry<String, VariableReplaceAdapter<?>> entrySet : globals.entrySet()) {
            final String variable = entrySet.getKey();
            final String displayName = Settings.getItemName(variable);
            final ReplaceEvent replaceEvent = new ReplaceEvent(variable, false, false, displayName, -1);

            final VariableReplaceAdapter<? extends Plugin> globalReplacer = entrySet.getValue();
            globalReplacer.onReplace(null, variable, replaceEvent);
            if (replaceEvent.isModified()) {
                updateScore(variable, replaceEvent.getScore());
            }
        }
    }

    protected Map<Class<? extends VariableReplaceAdapter<?>>, String> getDefaults() {
        return DEFAULTS;
    }

    protected Map<String, VariableReplaceAdapter<? extends Plugin>> getReplacers() {
        return specificReplacer;
    }

    protected boolean registerDefault(Class<? extends VariableReplaceAdapter<?>> replacerClass, String pluginName) {
        try {
            final VariableReplaceAdapter<?> instance = createInstance(replacerClass);
            register(instance);
            if (instance instanceof Listener) {
                Plugin replacerPlugin = Bukkit.getPluginManager().getPlugin(pluginName);
                if (pluginName.isEmpty()) {
                    //If it's empty it's one of our replacers
                    replacerPlugin = plugin;
                }
                Bukkit.getPluginManager().registerEvents((Listener) instance, replacerPlugin);
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

    private void getScoreLegacy(Player player, String variable, ReplaceEvent replaceEvent)
            throws UnknownVariableException {
        for (Iterator<VariableReplaceAdapter<?>> iterator = legacyReplacers.iterator(); iterator.hasNext();) {
            final VariableReplaceAdapter<?> legacyReplacer = iterator.next();

            try {
                legacyReplacer.onReplace(player, variable, replaceEvent);
            } catch (LinkageError linkageError) {
                plugin.getLogger().log(Level.WARNING, Lang.get("replacerException", legacyReplacer), linkageError);
                iterator.remove();
            } catch (Exception exception) {
                plugin.getLogger().log(Level.WARNING, Lang.get("replacerException", legacyReplacer), exception);
                iterator.remove();
            }

            if (replaceEvent.isModified()) {
                specificReplacer.put(variable, legacyReplacer);
                legacyReplacer.getVariables().add(variable);
                //fast return
                return;
            }
        }

        if (!replaceEvent.isModified()) {
            throw new UnknownVariableException("Variable '" + variable + "' not found");
        }
    }

    //add all replacers that are in the defaults map
    private void addDefaultReplacers() {
        final Set<String> replacersName = Sets.newHashSet();
        for (Map.Entry<Class<? extends VariableReplaceAdapter<?>>, String> entry : DEFAULTS.entrySet()) {
            final String pluginName = entry.getValue();
            //Check if the plugin is available and active
            if (pluginName.isEmpty() || Bukkit.getPluginManager().isPluginEnabled(pluginName)) {
                final Class<? extends VariableReplaceAdapter<?>> clazz = entry.getKey();
                if (registerDefault(clazz, pluginName)) {
                    //just add it if it was succesfull
                    replacersName.add(clazz.getSimpleName());
                }
            }
        }

        //log registered replacers
        plugin.getLogger().log(Level.INFO, "Registered replacers: {0}", replacersName);
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
