package com.github.games647.scoreboardstats.config;

import com.google.common.collect.Maps;

import java.util.Map;

public class SidebarConfig {

    private String displayName;

    private Map<String, VariableItem> itemsByVariable = Maps.newHashMapWithExpectedSize(15);

    public SidebarConfig(String displayName) {
        this.displayName = displayName;
    }

    public String getTitle() {
        return displayName;
    }

    public void setTitle(String displayName) {
        this.displayName = displayName;
    }

    public Map<String, VariableItem> getItemsByVariable() {
        return itemsByVariable;
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
