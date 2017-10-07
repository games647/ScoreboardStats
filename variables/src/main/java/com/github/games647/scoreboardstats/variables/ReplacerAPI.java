package com.github.games647.scoreboardstats.variables;

import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.slf4j.Logger;

import static org.bukkit.event.EventPriority.HIGHEST;

public abstract class ReplacerAPI {

    protected final Logger logger;
    protected final Map<String, Replacer> replacers = Maps.newHashMap();

    public ReplacerAPI(Logger logger) {
        this.logger = logger;
    }

    public void register(Replacer replacer) {
        replacers.put(replacer.getVariable(), replacer);

        for (EventReplacer<? extends Event> eventReplacer : replacer.getEventsReplacers().values()) {
            Class<? extends Event> eventClass = eventReplacer.getEventClass();
            Plugin plugin = replacer.getPlugin();

            EventExecutor executor = (listener, event) -> eventReplacer.execute(this, event);

            Listener listener = new Listener() {};

            PluginManager pluginManager = Bukkit.getPluginManager();
            pluginManager.registerEvent(eventClass, listener, HIGHEST, executor, plugin, true);
        }
    }

    public void unregister(String variable) {
        replacers.remove(variable);
    }

    public void unregisterAll(Plugin disablePlugin) {
        Iterator<Replacer> iterator = replacers.values().iterator();
        while (iterator.hasNext()) {
            Plugin plugin = iterator.next().getPlugin();
            if (plugin == disablePlugin) {
                iterator.remove();
            }
        }
    }

    public void forceUpdate(String variable, int score) {
        Bukkit.getOnlinePlayers().forEach(player -> forceUpdate(player, variable, score));
    }

    public void forceUpdate(String variable, String value) {
        Bukkit.getOnlinePlayers().forEach(player -> forceUpdate(player, variable, value));
    }

    public abstract void forceUpdate(Player player, String variable, int score);
    public abstract void forceUpdate(Player player, String variable, String value);

    public Optional<String> replace(Player player, String variable, boolean complete)
            throws UnknownVariableException, ReplacerException {
        Replacer replacer = this.replacers.get(variable);
        if (replacer == null) {
            throw new UnknownVariableException(variable);
        }

        if (!complete && replacer.isGlobal() || replacer.isEventVariable() || replacer.isConstant()) {
            return Optional.empty();
        }

        try {
            return Optional.of(replacer.replace(player));
        } catch (Exception ex) {
            throw new ReplacerException(ex);
        }
    }

    public OptionalInt scoreReplace(Player player, String variable, boolean complete)
            throws UnknownVariableException, ReplacerException {
        Replacer replacer = this.replacers.get(variable);
        if (replacer == null) {
            throw new UnknownVariableException(variable);
        }

        if (!complete && replacer.isGlobal() || replacer.isEventVariable() || replacer.isConstant()) {
            return OptionalInt.empty();
        }

        try {
            return OptionalInt.of(replacer.scoreReplace(player));
        } catch (Exception ex) {
            throw new ReplacerException(ex);
        }
    }
}
