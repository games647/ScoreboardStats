package com.github.games647.scoreboardstats.pvpstats;

import com.avaje.ebean.EbeanServer;
import com.github.games647.variables.Data;
import com.github.games647.variables.VariableList;

import java.util.HashMap;
import java.util.Map;

import static com.github.games647.scoreboardstats.ScoreboardStats.getSettings;

public final class Database {

	private static final Map<String, PlayerCache> CACHE = new HashMap<String, PlayerCache>(10);
	private static EbeanServer databaseInstance;

	public static void setDatabase(final EbeanServer base) {
		databaseInstance = base;
	}

	public static PlayerCache getCache(final String name) {
		if (!CACHE.containsKey(name))
			loadAccount(name);
		return CACHE.get(name);
	}

	public static void loadAccount(final String name) {
		if (CACHE.containsKey(name)) {
			return;
		}

		final PlayerStats stats = databaseInstance.find(PlayerStats.class).where().eq(Data.STATS_NAME, name).findUnique();

		CACHE.put(name, (stats == null) ? new PlayerCache() : new PlayerCache(stats.getKills(), stats.getMobkills(), stats.getDeaths(), stats.getKillstreak(), getRank(stats)));
	}

	public static int getKdr(final String name) {
		final PlayerCache stats = getCache(name);

		return (stats == null) ? 0
				: stats.getDeaths() == 0 ? stats.getKills() : stats.getKills() / stats.getDeaths();
	}

	public static void saveAccount(final String name, final boolean remove) {
		if (!getSettings().isPvpStats()) {
			return;
		}

		final PlayerCache playercache = CACHE.get(name);

		if (playercache == null) {
			return;
		} else if (remove) {
			CACHE.remove(name);
		}

		if (playercache.getKills() == 0
				&& playercache.getDeaths() == 0
				&& playercache.getMob() == 0) { //There are no need to save these data
			return;
		}

		PlayerStats stats = databaseInstance.find(PlayerStats.class).where().eq(Data.STATS_NAME, name).findUnique();

		if (stats == null) {
			stats = new PlayerStats();
			stats.setPlayername(name);
		}

		if (stats.getDeaths() == playercache.getDeaths()
				&& stats.getKills() == playercache.getKills()
				&& stats.getMobkills() == playercache.getMob()
				&& stats.getKillstreak() == playercache.getStreak()) {
			return; //No dates have been changed so there is no need to save the dates.
		}

		stats.setDeaths(playercache.getDeaths());
		stats.setKills(playercache.getKills());
		stats.setMobkills(playercache.getMob());
		stats.setKillstreak(playercache.getStreak());
		databaseInstance.save(stats);
	}

	public static void saveAll() {
		if (!getSettings().isPvpStats()) {
			return;
		}

		for (final String playername : CACHE.keySet()) {
			saveAccount(playername, false);
		}

		CACHE.clear();
	}

	public static Map<String, Integer> getTop() {
		final String type = getSettings().getTopType();
		final Map<String, Integer> top = new HashMap<String, Integer>(getSettings().getTopitems());
		for (final PlayerStats stats : databaseInstance.find(PlayerStats.class).orderBy(getRowName(type) + " desc").setMaxRows(getSettings().getTopitems()).findList()) {
			top.put(stats.getPlayername(), stats.get(getRowName(type)));

		}
		return top;
	}

	private static String getRowName(String type) {
		if (VariableList.KILLSTREAK.equals(type))
			return Data.COL_KILLSTREAK;
		else if (VariableList.MOB.equals(type))
			return Data.COL_MOB;
		else
			return Data.COL_KILL;
	}

	private static Integer getRank(PlayerStats PS) {
		final String type = getSettings().getTopType();
		if (VariableList.KILLSTREAK.equals(type)) {
			return 1 + databaseInstance.find(PlayerStats.class).where().gt(Data.COL_KILLSTREAK, PS.getKillstreak()).findRowCount();
		} else if (VariableList.MOB.equals(type)) {
			return 1 + databaseInstance.find(PlayerStats.class).where().gt(Data.COL_MOB, PS.getMobkills()).findRowCount();
		} else {
			return 1 + databaseInstance.find(PlayerStats.class).where().gt(Data.COL_KILL, PS.getKills()).findRowCount();
		}
	}
}
