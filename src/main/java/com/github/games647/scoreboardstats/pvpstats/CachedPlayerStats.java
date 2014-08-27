package com.github.games647.scoreboardstats.pvpstats;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

/**
 * Represents the playerstats wrapped as Bukkit metatdata associated to the player
 */
public class CachedPlayerStats extends FixedMetadataValue {

    public CachedPlayerStats(Plugin owningPlugin, PlayerStats value) {
        super(owningPlugin, value);
    }

    @Override
    public void invalidate() {
        //Just can be playerstats, because the instance can never been changed
        //and the above constructor pass only playerstats instances
        final PlayerStats stats = (PlayerStats) value();
        //submit it to the refresh queue, because this method could be called sync
        Database.saveAsync(stats);
    }
}
