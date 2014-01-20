package com.github.games647.scoreboardstats.variables;

import com.github.games647.scoreboardstats.Language;
import com.github.games647.scoreboardstats.ScoreboardStats;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public final class ReplaceManager implements Listener {

    private final Map<Replaceable, String> replacers = Maps.newHashMap();
    private final Map<Class<? extends Replaceable>, String> defaults;

    public ReplaceManager() {
        final Map<Class<? extends Replaceable>, String> tempMap = Maps.newHashMap();
        tempMap.put(GeneralVariables.class, "ScoreboardStats");
        tempMap.put(PlayerPingVariable.class, "ScoreboardStats");
        tempMap.put(StatsVariables.class, "ScoreboardStats");
//        tempMap.put(new TicksVariable(), "ScoreboardStats");

        tempMap.put(VaultVariables.class, "Vault");
        tempMap.put(FactionsVariables.class, "Factions");
        tempMap.put(HeroesVariables.class, "Heroes");
        tempMap.put(McmmoVariables.class, "mcMMO");
        tempMap.put(SimpleClansVariables.class, "SimpleClans");

        defaults = Collections.unmodifiableMap(tempMap);

        Bukkit.getServer().getPluginManager().registerEvents(this, ScoreboardStats.getInstance());
        addDefaultReplacer();
    }

    /**
     * @param replacer the variable replacer
     * @param pluginName the name of the associated plugin
     *
     * @throws NullPointerException if replacer is null
     * @throws NullPointerException if pluginName is null
     * @throws IllegalArgumentException if plugin isn't available or activated
     * @throws IllegalArgumentException if pluginName is empty
     * @throws IllegalStateException if replacer is already registered
     */
    public void register(Replaceable replacer, String pluginName)
            throws NullPointerException, IllegalArgumentException, IllegalStateException {
        Preconditions.checkNotNull(replacer, "replacer cannot be null");
        Preconditions.checkNotNull(pluginName, "pluginName cannot be null");

        Preconditions.checkArgument(!pluginName.isEmpty(), "the pluginName cannot be empty");
        Preconditions.checkArgument(isPluginAvailble(pluginName), "this plugin isn't available or activated");

        Preconditions.checkState(!replacers.containsKey(replacer), "this replacer is already registered");

        replacers.put(replacer, pluginName);
    }

    public void unregister(Replaceable replacer) {
        //fail safe
        if (replacers.containsKey(replacer)) {
            replacers.remove(replacer);
        }
    }

    public int getScore(Player player, String variable) throws UnknownVariableException {
        final Iterator<Map.Entry<Replaceable, String>> iter = replacers.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<Replaceable, String> entry = iter.next();
            final Replaceable replacer = entry.getKey();
            try {
                final int scoreValue = replacer.getScoreValue(player, variable);
                if (scoreValue != Replaceable.UNKOWN_VARIABLE) {
                    return scoreValue;
                }
            } catch (Exception e) {
                iter.remove();

                final Logger logger = ScoreboardStats.getInstance().getLogger();
                logger.log(Level.WARNING, Language.get("replacerException", replacer), e);
            }
        }

        throw new UnknownVariableException();
    }

    public final void addDefaultReplacer() {
        for (Map.Entry<Class<? extends Replaceable>, String> entry: defaults.entrySet()) {
            final String pluginName = entry.getValue();
            if (isPluginAvailble(pluginName)) {
                registerDefault(entry.getKey(), pluginName);
            }
        }
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent enableEvent) {
        final String enablePluginName = enableEvent.getPlugin().getName();
        for (Map.Entry<Class<? extends Replaceable>, String> entry: defaults.entrySet()) {
            final String pluginName = entry.getValue();
            if (enablePluginName.equalsIgnoreCase(entry.getValue())) {
                registerDefault(entry.getKey(), pluginName);
            }
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent disableEvent) {
        final String disablePluginName = disableEvent.getPlugin().getName();

        final Iterator<Map.Entry<Replaceable, String>> iter = replacers.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<Replaceable, String> entry = iter.next();
            if (disablePluginName.equalsIgnoreCase(entry.getValue())) {
                iter.remove();
            }
        }
    }

    private boolean isPluginAvailble(String pluginName) {
        return Bukkit.getServer().getPluginManager().isPluginEnabled(pluginName);
    }

    private void registerDefault(Class<? extends Replaceable> replacerClass, String pluginName) {
        try {
            final Replaceable instance = replacerClass.newInstance();
            if (!replacers.containsKey(instance)) {
                register(instance, pluginName);
            }
        } catch (Exception ex) {
            ScoreboardStats.getInstance().getLogger()
                    .log(Level.WARNING, Language.get("noRegister"), ex);
        }
    }

    public interface Replaceable {
        int UNKOWN_VARIABLE = -1337; // ^^

        int getScoreValue(Player player, String variable);
    }
}

