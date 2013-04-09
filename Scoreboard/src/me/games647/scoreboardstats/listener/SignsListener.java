package me.games647.scoreboardstats.listener;

import de.blablubbabc.insigns.Changer;
import de.blablubbabc.insigns.InSigns;
import me.games647.scoreboardstats.api.Database;
import org.bukkit.entity.Player;

public final class SignsListener {

    public SignsListener(final org.bukkit.plugin.Plugin insignsPlugin) {

        final String permission = "scoreboardstats.sign";
        final InSigns insigns = (InSigns) insignsPlugin;

        insigns.addChanger(new Changer("[Kills]", permission) {
            @Override
            public String getValue(final Player player) {
                return String.valueOf(Database.checkAccount(player.getName()).getKills());
            }
        });

        insigns.addChanger(new Changer("[Deaths]", permission) {
            @Override
            public String getValue(final Player player) {
                return String.valueOf(Database.checkAccount(player.getName()).getDeaths());
            }
        });

        insigns.addChanger(new Changer("[Mob]", permission) {
            @Override
            public String getValue(final Player player) {
                return String.valueOf(Database.checkAccount(player.getName()).getMobkills());
            }
        });

        insigns.addChanger(new Changer("[KDR]", permission) {
            @Override
            public String getValue(final Player player) {
                return String.valueOf(Database.getKdr(player.getName()));
            }
        });
    }
}
