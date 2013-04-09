package me.games647.scoreboardstats.listener;

import de.blablubbabc.insigns.Changer;
import static me.games647.scoreboardstats.api.Database.checkAccount;
import static me.games647.scoreboardstats.api.Database.getKdr;
import org.bukkit.entity.Player;

public final class SignsListener {

    public SignsListener(final de.blablubbabc.insigns.InSigns instance) {

        final String permission = "scoreboardstats.sign";

        instance.addChanger(new Changer("[Kills]", permission) {
            @Override
            public String getValue(final Player player) {
                return String.valueOf(checkAccount(player.getName()).getKills());
            }
        });

        instance.addChanger(new Changer("[Deaths]", permission) {
            @Override
            public String getValue(final Player player) {
                return String.valueOf(checkAccount(player.getName()).getDeaths());
            }
        });

        instance.addChanger(new Changer("[Mob]", permission) {
            @Override
            public String getValue(final Player player) {
                return String.valueOf(checkAccount(player.getName()).getMobkills());
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
