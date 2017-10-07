package com.github.games647.scoreboardstats.defaults;

import com.github.games647.scoreboardstats.variables.DefaultReplacer;
import com.github.games647.scoreboardstats.variables.DefaultReplacers;
import com.github.games647.scoreboardstats.variables.ReplacerAPI;

import java.util.Collection;
import java.util.Optional;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;

import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

/**
 * Replace all variables that are associated with the SimpleClans plugin
 * <p>
 * https://dev.bukkit.org/bukkit-plugins/simpleclans/
 *
 * @see SimpleClans
 * @see ClanPlayer
 * @see Clan
 * @see ClanManager
 */
@DefaultReplacer(plugin = "SimpleClans")
public class SimpleClansVariables extends DefaultReplacers<SimpleClans> {

    private final ClanManager clanManager;

    public SimpleClansVariables(ReplacerAPI replaceManager, SimpleClans plugin) {
        super(replaceManager, plugin);

        clanManager = plugin.getClanManager();
    }

    @Override
    public void register() {
        register("kills").scoreSupply(player -> getClanPlayer(player)
                .map(clanPlayer -> clanPlayer.getCivilianKills()
                        + clanPlayer.getNeutralKills()
                        + clanPlayer.getRivalKills())
                .orElse(-1));

        register("deaths").scoreSupply(player -> getClanPlayer(player)
                .map(ClanPlayer::getDeaths)
                .orElse(-1));

        register("kdr").scoreSupply(player -> getClanPlayer(player)
                .map(ClanPlayer::getKDR)
                .map(NumberConversions::round)
                .orElse(-1));

        register("members").scoreSupply(player -> getClan(player)
                .map(Clan::getSize)
                .orElse(-1));

        register("clan_kdr").scoreSupply(player -> getClan(player)
                .map(Clan::getTotalKDR)
                .map(NumberConversions::round)
                .orElse(-1));

        register("clan_money").scoreSupply(player -> getClan(player)
                .map(Clan::getBalance)
                .map(NumberConversions::round)
                .orElse(-1));

        register("rivals").scoreSupply(player -> getClan(player)
                .map(Clan::getRivals)
                .map(Collection::size)
                .orElse(-1));

        register("allies").scoreSupply(player -> getClan(player)
                .map(Clan::getAllies)
                .map(Collection::size)
                .orElse(-1));

        register("members_online").scoreSupply(player -> getClan(player)
                .map(Clan::getOnlineMembers)
                .map(Collection::size)
                .orElse(-1));

        register("allies_total").scoreSupply(player -> getClan(player)
                .map(Clan::getAllAllyMembers)
                .map(Collection::size)
                .orElse(-1));

        register("clan_kills").scoreSupply(player -> getClan(player)
                .map(clan -> clan.getTotalCivilian()
                        + clan.getTotalNeutral()
                        + clan.getTotalRival())
                .orElse(-1));
    }

    private Optional<ClanPlayer> getClanPlayer(Player player) {
        return Optional.ofNullable(clanManager.getClanPlayer(player));
    }

    private Optional<Clan> getClan(Player player) {
        return getClanPlayer(player).map(ClanPlayer::getClan);
    }
}
