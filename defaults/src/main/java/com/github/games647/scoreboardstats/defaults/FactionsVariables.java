package com.github.games647.scoreboardstats.defaults;

import com.github.games647.scoreboardstats.Version;
import com.github.games647.scoreboardstats.variables.DefaultReplacer;
import com.github.games647.scoreboardstats.variables.DefaultReplacers;
import com.github.games647.scoreboardstats.variables.ReplacerAPI;
import com.github.games647.scoreboardstats.variables.UnsupportedPluginException;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.entity.MPlayer;

import java.util.Collection;
import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Replace all variables that are associated with the faction plugin
 * <p>
 * https://dev.bukkit.org/bukkit-plugins/factions/
 */
@DefaultReplacer(plugin = "Factions")
public class FactionsVariables extends DefaultReplacers<Plugin> {

    private final boolean newVersion;

    public FactionsVariables(ReplacerAPI replaceManager, Plugin plugin) throws UnsupportedPluginException {
        super(replaceManager, plugin);

        String version = plugin.getDescription().getVersion();
        newVersion = Version.compare("2", version) >= 0;

        //Version is between 2.0 and 2.7
        if (newVersion && Version.compare("2.7", version) < 0) {
            throw new UnsupportedPluginException("Due the newest changes from "
                    + "Factions, you have to upgrade your version to a version above 2.7. "
                    + "If explicitly want to use this version. Create a ticket on "
                    + "the project page of this plugin");
        }
    }

    //faction 2.7+
    private void registerNewFactions() {
        register("power")
                .scoreSupply(player -> getMPlayer(player)
                        .map(MPlayer::getPowerRounded)
                        .orElse(-1));

        register("f_power")
                .scoreSupply(player -> getNewFaction(player)
                        .map(com.massivecraft.factions.entity.Faction::getPowerRounded)
                        .orElse(-1));

        register("members")
                .scoreSupply(player -> getNewFaction(player)
                        .map(com.massivecraft.factions.entity.Faction::getMPlayers)
                        .map(Collection::size)
                        .orElse(-1));

        register("members_online")
                .scoreSupply(player -> getNewFaction(player)
                        .map(com.massivecraft.factions.entity.Faction::getMPlayers)
                        .map(Collection::size)
                        .orElse(-1));
    }

    //factions 1.6.9 and 1.8.2
    private void registerOldFactions() {
        register("power")
                .scoreSupply(player -> getFPlayer(player)
                        .map(FPlayer::getPowerRounded)
                        .orElse(-1));

        register("f_power")
                .scoreSupply(player -> getOldFaction(player)
                        .map(com.massivecraft.factions.Faction::getPowerRounded)
                        .orElse(-1));

        register("members")
                .scoreSupply(player -> getOldFaction(player)
                        .map(com.massivecraft.factions.Faction::getFPlayers)
                        .map(Collection::size)
                        .orElse(-1));

        register("members_online")
                .scoreSupply(player -> getOldFaction(player)
                        .map(com.massivecraft.factions.Faction::getFPlayers)
                        .map(Collection::size)
                        .orElse(-1));
    }

    @Override
    public void register() {
        if (newVersion) {
            registerNewFactions();
        } else {
            registerOldFactions();
        }
    }

    private Optional<MPlayer> getMPlayer(Player player) {
        return Optional.ofNullable(MPlayer.get(player));
    }

    private Optional<com.massivecraft.factions.entity.Faction> getNewFaction(Player player) {
        return getMPlayer(player).map(MPlayer::getFaction);
    }

    private Optional<FPlayer> getFPlayer(Player player) {
        return Optional.ofNullable(FPlayers.getInstance().getByPlayer(player));
    }

    private Optional<com.massivecraft.factions.Faction> getOldFaction(Player player) {
        return getFPlayer(player).map(FPlayer::getFaction);
    }
}
