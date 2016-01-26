package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.VariableReplaceAdapter;

import java.util.Calendar;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Reprents a replacer for non Minecraft related variables
 */
public class GeneralVariables extends VariableReplaceAdapter<Plugin> {

    //From bytes to mega bytes
    private static final int MB_CONVERSION = 1_024 * 1_024;

    public GeneralVariables() {
        super(null, "", true, true, false, "free_ram", "max_ram", "used_ram", "usedram", "date");
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        if ("free_ram".equals(variable)) {
            //casting should be made after division
            replaceEvent.setScore((int) (Runtime.getRuntime().freeMemory() / MB_CONVERSION));
        } else if ("max_ram".equals(variable)) {
            //casting should be made after division
            replaceEvent.setScore((int) (Runtime.getRuntime().maxMemory() / MB_CONVERSION));
        } else if ("used_ram".equals(variable)) {
            long usedRam = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory();
            //casting should be made after division
            replaceEvent.setScore((int) (usedRam / MB_CONVERSION));
            //convert to megabytes
        } else if ("usedram".equals(variable)) {
            //casting should be made after division
            Runtime runtime = Runtime.getRuntime();
            //percent calculation
            replaceEvent.setScore((int) ((runtime.maxMemory() - runtime.freeMemory()) * 100 / runtime.maxMemory()));
        } else if ("date".equals(variable)) {
            //Get the current date
            replaceEvent.setScore(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        }
    }
}
