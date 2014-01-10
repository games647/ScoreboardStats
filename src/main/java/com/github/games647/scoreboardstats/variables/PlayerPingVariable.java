package com.github.games647.scoreboardstats.variables;

import com.github.games647.scoreboardstats.ScoreboardStats;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.fusesource.jansi.Ansi;

public class PlayerPingVariable implements ReplaceManager.Replaceable {

    @Override
    public int getScoreValue(Player player, String variable) {
        if ("%ping%".equals(variable)) {
            return getReflectionPing(player);
        }

        return UNKOWN_VARIABLE;
    }

    private int getReflectionPing(Player player) {
        try {
            final Method getHandleMethod = player.getClass().getDeclaredMethod("getHandle");
            final Object entityPlayer = getHandleMethod.invoke(player);
            final Field pingField = entityPlayer.getClass().getDeclaredField("ping");

            return pingField.getInt(entityPlayer);
        } catch (Exception ex) {
            ScoreboardStats.getInstance().getLogger()
                    .log(Level.SEVERE, Ansi.ansi().fg(Ansi.Color.RED)
                    + "Unable to get the ping for a player."
                    + Ansi.ansi().fg(Ansi.Color.DEFAULT), ex);

            throw new RuntimeException(ex); //Rethrow to handle it in ReplaceManager
        }
    }
}
