package com.github.games647.scoreboardstats.config;

import com.google.common.collect.Maps;

import java.util.Map;

import org.bukkit.ChatColor;

public class SidebarConfig {

    private final Map<String, VariableItem> itemsByName = Maps.newHashMapWithExpectedSize(15);
    private final Map<String, VariableItem> itemsByVariable = Maps.newHashMapWithExpectedSize(15);
    private String displayName;

    public SidebarConfig(String displayName) {
        this.displayName = ChatColor.translateAlternateColorCodes('&', displayName);
    }

    public String getTitle() {
        return displayName;
    }

    public void setTitle(String displayName) {
        this.displayName = ChatColor.translateAlternateColorCodes('&', displayName);
    }

    public void addItem(String displayName, int score) {
        String colorName = ChatColor.translateAlternateColorCodes('&', displayName);

        VariableItem variableItem = new VariableItem(false, null, colorName, score);
        itemsByName.put(colorName, variableItem);
    }

    public void addVariableItem(boolean textVariable, String variable, String displayText, int defaultScore) {
        String coloredDisplay = ChatColor.translateAlternateColorCodes('&', displayText);

        VariableItem variableItem = new VariableItem(textVariable, variable, coloredDisplay, defaultScore);
        itemsByName.put(coloredDisplay, variableItem);
        itemsByVariable.put(variable, variableItem);
    }

    public void remove(VariableItem variableItem) {
        itemsByName.remove(variableItem.getDisplayText());
        itemsByVariable.remove(variableItem.getVariable());
    }

    public Map<String, VariableItem> getItemsByName() {
        return itemsByName;
    }

    public Map<String, VariableItem> getItemsByVariable() {
        return itemsByVariable;
    }

    public int size() {
        return itemsByName.size();
    }

    public void clear() {
        itemsByVariable.clear();
    }

    @Override
    public String toString() {
        return "SidebarConfig{" + "displayName="
                + displayName + ", itemsByVariable="
                + itemsByVariable
                + '}';
    }
}
