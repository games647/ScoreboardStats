package com.github.games647.scoreboardstats.defaults;

import com.github.games647.scoreboardstats.variables.DefaultReplacer;
import com.github.games647.scoreboardstats.variables.DefaultReplacers;
import com.github.games647.scoreboardstats.variables.ReplacerAPI;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.external.EZPlaceholderHook;

import org.bukkit.plugin.Plugin;

@DefaultReplacer(plugin = "PlaceholderAPI")
public class PlaceHolderVariables extends DefaultReplacers<Plugin> {

    public PlaceHolderVariables(ReplacerAPI replaceManager, Plugin plugin) {
        super(replaceManager, plugin);
    }

    @Override
    public void register() {
        Set<String> variables = Sets.newHashSet();

        Collection<PlaceholderHook> hooks = PlaceholderAPI.getPlaceholders().values();
        for (PlaceholderHook hook : hooks) {
            String variablePrefix = null;
            if (hook instanceof EZPlaceholderHook) {
                variablePrefix = ((EZPlaceholderHook) hook).getPlaceholderName();
            } else if (hook instanceof PlaceholderExpansion) {
                variablePrefix = ((PlaceholderExpansion) hook).getIdentifier();
            }

            if (variablePrefix != null) {
                variables.add(variablePrefix + "_*");
            }
        }

        for (String variable : variables) {
            register(variable).supply(player -> PlaceholderAPI.setPlaceholders(player,'%' + variable + '%'));
        }
    }
}
