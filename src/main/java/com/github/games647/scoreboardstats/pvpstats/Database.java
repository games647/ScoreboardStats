package com.github.games647.scoreboardstats.pvpstats;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import com.github.games647.scoreboardstats.Language;
import com.github.games647.scoreboardstats.ScoreboardStats;
import com.github.games647.scoreboardstats.Settings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.persistence.PersistenceException;

public final class Database {

    private static EbeanServer databaseInstance;
    private static DatabaseConfiguration dbConfiguration;

    private static final Cache<String, PlayerCache> CACHE = CacheBuilder.newBuilder()
            .maximumSize(512)
            .expireAfterAccess(15, TimeUnit.MINUTES)
            .removalListener(RemoveListener.getNewInstace())
            .build(new CacheLoader<String, PlayerCache>() {
                @Override
                public PlayerCache load(String playerName) {
                    ScoreboardStats.getInstance().getLogger().warning(Language.get("synchLoading"));
                    final PlayerStats stats = databaseInstance.find(PlayerStats.class).where().eq("playername", playerName).findUnique();
                    if (stats == null) {
                        return new PlayerCache();
                    }

                    final int kills = stats.getKills();
                    final int deaths = stats.getDeaths();
                    final int mobKills = stats.getMobkills();
                    final int killstreak = stats.getKillstreak();
                    return new PlayerCache(kills, mobKills, deaths, killstreak);
                }
            });

    public static PlayerCache getCacheIfAbsent(String name) {
        if (CACHE.asMap().containsKey(name)) {
            return CACHE.getUnchecked(name);
        }

        return null;
    }

    public static void loadAccount(String name) {
        final Map<String, PlayerCache> cache = CACHE.asMap();
        if (cache.containsKey(name)) {
            return;
        }

        final PlayerStats stats = databaseInstance.find(PlayerStats.class).where().eq("playername", name).findUnique();
        if (stats == null) {
            cache.put(name, new PlayerCache());
        } else {
            final int kills = stats.getKills();
            final int deaths = stats.getDeaths();
            final int mobKills = stats.getMobkills();
            final int killstreak = stats.getKillstreak();
            cache.put(name, new PlayerCache(kills, mobKills, deaths, killstreak));
        }
    }

    public static void saveAll() {
        if (Settings.isPvpStats()) {
            CACHE.invalidateAll();
        }
    }

    public static Map<String, Integer> getTop() {
        final String type = Settings.getTopType();
        final Map<String, Integer> top = new HashMap<String, Integer>(Settings.getTopitems());
        if ("%killstreak%".equals(type)) {
            for (final PlayerStats stats : databaseInstance.find(PlayerStats.class).orderBy("killstreak desc").setMaxRows(Settings.getTopitems()).findList()) {
                top.put(stats.getPlayername(), stats.getKillstreak());
            }
        } else if ("%mob%".equals(type)) {
            for (final PlayerStats stats : databaseInstance.find(PlayerStats.class).orderBy("mobkills desc").setMaxRows(Settings.getTopitems()).findList()) {
                top.put(stats.getPlayername(), stats.getMobkills());
            }
        } else {
            for (final PlayerStats stats : databaseInstance.find(PlayerStats.class).orderBy("kills desc").setMaxRows(Settings.getTopitems()).findList()) {
                top.put(stats.getPlayername(), stats.getKills());
            }
        }

        return top;
    }

    public static void setupDatabase(ScoreboardStats pluginInstance) {
        if (Settings.isPvpStats()) {
            dbConfiguration = new DatabaseConfiguration(pluginInstance);
            dbConfiguration.loadConfiguration();

            final ClassLoader previous = Thread.currentThread().getContextClassLoader();

            Thread.currentThread().setContextClassLoader(pluginInstance.getClassLoaderBypass());
            final EbeanServer database = EbeanServerFactory.create(dbConfiguration.getServerConfig());
            Thread.currentThread().setContextClassLoader(previous);

            try {
                database.find(PlayerStats.class).findRowCount();
            } catch (PersistenceException ex) {
                pluginInstance.getLogger().info(Language.get("newDatabase"));
                final DdlGenerator gen = ((SpiEbeanServer) database).getDdlGenerator();
                gen.runScript(false, gen.generateCreateDdl());
            }

            databaseInstance = database;
        }
    }

    protected static EbeanServer getDatabaseInstance() {
        return databaseInstance;
    }

    private Database() {
        //Singleton
    }
}
