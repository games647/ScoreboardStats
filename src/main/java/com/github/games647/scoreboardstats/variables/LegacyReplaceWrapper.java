package com.github.games647.scoreboardstats.variables;

import org.bukkit.entity.Player;

public class LegacyReplaceWrapper implements VariableReplacer {

    private final Replaceable oldReplacer;

    public LegacyReplaceWrapper(Replaceable oldReplacer) {
        this.oldReplacer = oldReplacer;
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        final int scoreValue = oldReplacer.getScoreValue(player, '%' + variable + '%');
        if (scoreValue != Replaceable.UNKOWN_VARIABLE) {
            replaceEvent.setScore(scoreValue);
        }
    }

    @Override
    public int hashCode() {
        //make it possible to remove them using the Replaceable instance
        return oldReplacer.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return oldReplacer.equals(obj);
    }

    @Override
    public String toString() {
        return "LegacyReplaceWrapper{" + "oldReplacer=" + oldReplacer + '}';
    }
}
