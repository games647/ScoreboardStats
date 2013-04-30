package me.games647.scoreboardstats.api;

import static me.games647.scoreboardstats.ScoreboardStats.getInstance;
import static me.games647.scoreboardstats.ScoreboardStats.getSettings;
import me.games647.scoreboardstats.api.pvpstats.TempScoreShow;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getScheduler;
import static org.bukkit.ChatColor.translateAlternateColorCodes;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public final class Score {

    public static void createScoreboard(final Player player, final boolean type) {
        Objective oldobjective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);

        if (oldobjective != null) {
            oldobjective.unregister();
        }

        oldobjective = player.getScoreboard().registerNewObjective("ScoreboardStats", "dummy");
        oldobjective.setDisplaySlot(DisplaySlot.SIDEBAR);


        if (type) {
            if (getSettings().isTempscoreboard()) {
                getScheduler().runTaskLater(getInstance(), new TempScoreShow(player), getSettings().getTempshow() * 20L);
            }

            oldobjective.setDisplayName(translateAlternateColorCodes('&', getSettings().getTitle()));
            getSettings().sendUpdate(player);
            return;
        }

        oldobjective.setDisplayName(translateAlternateColorCodes('&', getSettings().getTemptitle()));
    }

    public static void sendScore(final Player player, final String title, final int value, final boolean type) {
        final Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);

        if ((objective == null) || (!objective.getName().equalsIgnoreCase("ScoreboardStats"))) {
            return;
        }

        final org.bukkit.scoreboard.Score score = objective.getScore(Bukkit.getOfflinePlayer(title));

        if (score.getScore() == value) {
            return;
        }

        score.setScore(value);
        player.setScoreboard(objective.getScoreboard());
    }

//    public static void sendRemoveScore(final Player player, final String title) {
//        final Packet207SetScoreboardScore packet = new Packet207SetScoreboardScore();
//
//        packet.a = translateAlternateColorCodes('&', title);
//        packet.b = getSettings().getTitle();
//        packet.d = 1;
//
//        con.sendPacket(packet);
//    }
}
