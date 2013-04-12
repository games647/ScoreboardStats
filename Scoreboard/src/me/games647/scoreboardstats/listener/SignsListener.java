package me.games647.scoreboardstats.listener;

import de.blablubbabc.insigns.Changer;
import me.games647.scoreboardstats.api.pvpstats.Database;
import static me.games647.scoreboardstats.api.pvpstats.Database.getKdr;
import org.bukkit.entity.Player;

public final class SignsListener {

    public SignsListener(final de.blablubbabc.insigns.InSigns instance) {

        final String permission = "scoreboardstats.sign";

        instance.addChanger(new Changer("[Kills]", permission) {
            @Override
            public String getValue(final Player player) {
                return String.valueOf(Database.getCache(player.getName()).getKills());
            }
        });

        instance.addChanger(new Changer("[Deaths]", permission) {
            @Override
            public String getValue(final Player player) {
                return String.valueOf(Database.getCache(player.getName()).getDeaths());
            }
        });

        instance.addChanger(new Changer("[Mob]", permission) {
            @Override
            public String getValue(final Player player) {
                return String.valueOf(Database.getCache(player.getName()).getMob());
            }
        });

        instance.addChanger(new Changer("[KDR]", permission) {
            @Override
            public String getValue(final Player player) {
                return String.valueOf(getKdr(player.getName()));
            }
        });
    }
}
