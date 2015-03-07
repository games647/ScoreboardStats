package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.VariableReplacer;

import java.util.Calendar;

import org.bukkit.entity.Player;

public class GeneralVariables implements VariableReplacer {

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        if ("free_ram".equals(variable)) {
            //casting should be made after division
            replaceEvent.setScore((int) (Runtime.getRuntime().freeMemory() / (1024 * 1024)));
        }

        if ("max_ram".equals(variable)) {
            //casting should be made after division
            replaceEvent.setScore((int) (Runtime.getRuntime().maxMemory() / (1024 * 1024)));
        }

        if ("used_ram".equals(variable)) {
            final long usedRam = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory();
            //casting should be made after division
            replaceEvent.setScore((int) (usedRam / (1024 * 1024)));
            //convert to megabytes
        }

        if ("usedram".equals(variable)) {
            //casting should be made after division
            final Runtime runtime = Runtime.getRuntime();
            replaceEvent.setScore((int) ((runtime.maxMemory() - runtime.freeMemory()) * 100 / runtime.maxMemory()));
        }

        if ("date".equals(variable)) {
            //Get the current date
            replaceEvent.setScore(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        }
    }
}
