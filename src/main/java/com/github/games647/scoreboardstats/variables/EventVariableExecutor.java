package com.github.games647.scoreboardstats.variables;

import com.github.games647.scoreboardstats.ScoreboardStats;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

public class EventVariableExecutor implements EventExecutor {

    protected final ScoreboardStats plugin;
    protected final VariableReplacer replacer;
    protected final String variable;

    public EventVariableExecutor(ScoreboardStats plugin, VariableReplacer replacer, String variable) {
        this.plugin = plugin;
        this.replacer = replacer;
        this.variable = variable;
    }

    @Override
    public void execute(Listener listener, Event event) throws EventException {
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                ReplaceEvent replaceEvent = new ReplaceEvent(variable, false, "", 0);
                replacer.onReplace(null, variable, replaceEvent);

                plugin.getReplaceManager().updateScore(variable, replaceEvent.getScore());
            }
        });
    }
}
