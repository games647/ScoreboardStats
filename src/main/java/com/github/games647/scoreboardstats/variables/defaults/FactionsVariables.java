package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.Version;
import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.UnsupportedPluginException;
import com.github.games647.scoreboardstats.variables.VariableReplaceAdapter;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.entity.MPlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Replace all variables that are associated with the faction plugin
 */
public class FactionsVariables extends VariableReplaceAdapter<Plugin> {

    private final boolean newVersion;

    /**
     * Creates a new faction replacer
     */
    public FactionsVariables() {
        super(Bukkit.getPluginManager().getPlugin("Factions")
                , "power", "f_power", "members_online", "members");

        final String version = getPlugin().getDescription().getVersion();
        newVersion = Version.compare("2", version) >= 0;

        //Version is between 2.0 and 2.7
        if (newVersion && Version.compare("2.7", version) < 0) {
            throw new UnsupportedPluginException("Due the newest changes from "
                    + "Factions, you have to upgrade your version to a version above 2.7. "
                    + "If explicity want to use this version. Create a ticket on "
                    + "the project page of this plugin");
        }
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        if (newVersion) {
            getNewFactionScore(player, variable, replaceEvent);
        } else {
            getOldFactionScore(player, variable, replaceEvent);
        }
    }

    //faction 2.7+
    private void getNewFactionScore(Player player, String variable, ReplaceEvent replaceEvent) {
        //If factions doesn't track the player yet return -1
        final MPlayer mplayer = MPlayer.get(player);
        if (mplayer == null) {
            return;
        }

        if ("power".equals(variable)) {
            replaceEvent.setScore(mplayer.getPowerRounded());
        } else {
            final com.massivecraft.factions.entity.Faction faction = mplayer.getFaction();
            if (faction == null) {
                return;
            }

            if ("f_power".equals(variable)) {
                replaceEvent.setScore(faction.getPowerRounded());
            } else if ("members".equals(variable)) {
                replaceEvent.setScore(faction.getMPlayers().size());
            } else if ("members_online".equals(variable)) {
                replaceEvent.setScore(faction.getOnlinePlayers().size());
            }
        }
    }

    //factions 1.6.9 and 1.8.2
    private void getOldFactionScore(Player player, String variable, ReplaceEvent replaceEvent) {
        //If factions doesn't track the player yet return -1
        final FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        if (fPlayer == null) {
            return;
        }

        if ("power".equals(variable)) {
            replaceEvent.setScore(fPlayer.getPowerRounded());
        } else {
            final com.massivecraft.factions.Faction faction = fPlayer.getFaction();
            if (faction == null) {
                return;
            }

            if ("f_power".equals(variable)) {
                replaceEvent.setScore(faction.getPowerRounded());
            } else if ("members".equals(variable)) {
                replaceEvent.setScore(faction.getFPlayers().size());
            } else if ("members_online".equals(variable)) {
                replaceEvent.setScore(faction.getOnlinePlayers().size());
            }
        }
    }
}
