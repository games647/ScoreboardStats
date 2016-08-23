package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;

import java.util.Collection;
import java.util.Set;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import me.clip.placeholderapi.internal.IPlaceholderHook;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlaceHolderVariables extends DefaultReplaceAdapter<Plugin> {
    
    private static String[] getVariablesPrefixes() {
        Set<String> variables = Sets.newHashSet();

        Collection<PlaceholderHook> hooks = PlaceholderAPI.getPlaceholders().values();
        for (PlaceholderHook hook : hooks) {
            String variablePrefix = null;
            if (hook instanceof EZPlaceholderHook) {
                variablePrefix = ((EZPlaceholderHook) hook).getPlaceholderName();
            } else if (hook instanceof IPlaceholderHook) {
                variablePrefix = ((IPlaceholderHook) hook).getIdentifier();
            }

            if (variablePrefix != null) {
                variables.add(variablePrefix + "_*");
            }
        }

        return variables.stream().toArray(size -> new String[size]);
    }

    public PlaceHolderVariables() {
        super(Bukkit.getPluginManager().getPlugin("PlaceholderAPI"), getVariablesPrefixes());
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        String replaced = PlaceholderAPI.setPlaceholders(player, '%' + variable + '%');
        //remove colors if inserted by the replace plugin
        String strip = ChatColor.stripColor(replaced);
        
        //try parse with a decimal in the case it's decimal if not it parsing will work too
        Double score = Doubles.tryParse(strip);
        if (score != null) {
            //is not text variable and so this could be parsed to a number
            replaceEvent.setScoreOrText(score.intValue());
        }
    }
}
