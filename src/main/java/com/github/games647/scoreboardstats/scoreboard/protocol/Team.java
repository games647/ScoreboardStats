package com.github.games647.scoreboardstats.scoreboard.protocol;

import com.google.common.collect.Sets;

import java.util.Set;

public class Team {

    private final String uniqueId;
    private final Set<String> players = Sets.newHashSet();

    private String prefix;
    private String suffix;

    Team(String uniqueId, String prefix, String suffix) {
        this.uniqueId = uniqueId;

        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getName() {
        return uniqueId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Set<String> getEntries() {
        return players;
    }

    public void addEntry(String name) {
        this.players.add(name);
    }

    public boolean removeEntry(String name) {
        return this.players.remove(name);
    }
}
