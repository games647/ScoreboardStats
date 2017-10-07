package com.github.games647.scoreboardstats.defaults;

import com.github.games647.scoreboardstats.variables.DefaultReplacer;
import com.github.games647.scoreboardstats.variables.DefaultReplacers;
import com.github.games647.scoreboardstats.variables.ReplacerAPI;

import java.util.Calendar;

import org.bukkit.plugin.Plugin;

/**
 * Represents a replacer for non Minecraft related variables
 */
@DefaultReplacer
public class GeneralVariables extends DefaultReplacers<Plugin> {

    //From bytes to mega bytes
    private static final int MB_CONVERSION = 1_024 * 1_024;

    private final Runtime runtime = Runtime.getRuntime();

    public GeneralVariables(ReplacerAPI replaceManager, Plugin plugin) {
        super(replaceManager, plugin);
    }

    @Override
    public void register() {
        register("free_ram").scoreSupply(() -> (int) (runtime.freeMemory() / MB_CONVERSION));

        register("max_ram").scoreSupply(() -> (int) (runtime.maxMemory() - runtime.freeMemory() / MB_CONVERSION));

        register("used_ram")
                .scoreSupply(() -> (int) ((runtime.maxMemory() - runtime.freeMemory()) * 100 / runtime.maxMemory()));

        register("date").scoreSupply(() -> Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    }
}
