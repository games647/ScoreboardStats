package com.github.games647.scoreboardstats.variables;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

public class EventVariableExecutor implements EventExecutor {

    protected final Plugin plugin;
    protected final ReplaceManager replaceManager;
    protected final VariableReplacer replacer;
    protected final String variable;

    public EventVariableExecutor(Plugin plugin, ReplaceManager replaceManager, VariableReplacer replacer
            , String variable) {
        this.plugin = plugin;
        this.replaceManager = replaceManager;
        this.replacer = replacer;
        this.variable = variable;
    }

    @Override
    public void execute(Listener listener, final Event event) throws EventException {
        Bukkit.getScheduler().runTask(plugin, () -> {
            Player eventPlayer = null;
            if (event instanceof PlayerEvent) {
                eventPlayer = ((PlayerEvent) event).getPlayer();
            }
            
            ReplaceEvent replaceEvent = new ReplaceEvent(variable, false, "", 0);
            replacer.onReplace(eventPlayer, variable, replaceEvent);
            
            if (eventPlayer == null) {
                replaceManager.updateScore(variable, replaceEvent.getScore());
            } else {
                replaceManager.updateScore(eventPlayer, variable, replaceEvent.getScore());
            }
        });
    }
}
