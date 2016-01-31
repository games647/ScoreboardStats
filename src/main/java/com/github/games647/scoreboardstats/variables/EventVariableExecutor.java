package com.github.games647.scoreboardstats.variables;

import com.github.games647.scoreboardstats.ScoreboardStats;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

public class EventVariableExecutor implements EventExecutor {

    private final ScoreboardStats plugin;

    public EventVariableExecutor(ScoreboardStats plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Listener listener, Event event) throws EventException {
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                //update by event
            }
        });
    }
}
