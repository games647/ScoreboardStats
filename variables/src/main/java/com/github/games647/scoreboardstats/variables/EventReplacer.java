package com.github.games647.scoreboardstats.variables;

import com.google.common.collect.Sets;

import java.util.Set;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;

class EventReplacer<T extends Event> {

    private final Replacer replacer;
    private final Class<T> eventClass;
    private final Set<Function<T, String>> functions = Sets.newHashSet();
    private final Set<Function<T, Integer>> scoreFunctions = Sets.newHashSet();

    public EventReplacer(Replacer replacer, Class<T> eventClass) {
        this.replacer = replacer;
        this.eventClass = eventClass;
    }

    public void addFct(Function<T, String> fct) {
        functions.add(fct);
    }

    public void addScoreFct(Function<T, Integer> fct) {
        scoreFunctions.add(fct);
    }

    public Set<Function<T, String>> getFunctions() {
        return functions;
    }

    public Set<Function<T, Integer>> getScoreFunctions() {
        return scoreFunctions;
    }

    public void execute(ReplacerAPI replaceManager, Event event) {
        executeUnsafe(replaceManager, eventClass.cast(event));
    }

    private void executeUnsafe(ReplacerAPI replaceManager, T event) {
        String variable = replacer.getVariable();
        for (Function<T, Integer> function : scoreFunctions) {
            int newScore = function.apply(event);
            if (replacer.isGlobal()) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    replaceManager.forceUpdate(player, variable, newScore);
                }
            } else if (event instanceof PlayerEvent) {
                replaceManager.forceUpdate(((PlayerEvent) event).getPlayer(), variable, newScore);
            }
        }
    }

    public Class<T> getEventClass() {
        return eventClass;
    }
}
