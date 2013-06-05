package com.github.games647.scoreboardstats.pvpstats;

public final class PlayerCache {

	private int kills;
	private int mob;
	private int deaths;
	private int streak;
	private int laststreak;
	private int rank;

	public PlayerCache(final int paramkills, final int parammob, final int paramdeaths, final int paramstreak, final int paramRank) {
		kills = paramkills;
		mob = parammob;
		deaths = paramdeaths;
		streak = paramstreak;
		rank = paramRank;
	}

	public PlayerCache() {
		//Do nothing, because all variables are automatically init as 0
	}

	public int getKills() {
		return kills;
	}

	public int getMob() {
		return mob;
	}

	public int getDeaths() {
		return deaths;
	}

	public int getStreak() {
		return streak;
	}

	public void onKill() {
		kills++;
		laststreak++;
	}

	public void increaseMob() {
		mob++;
	}

	public int getLastStreak() {
		return laststreak;
	}

	public int getRank() {
		return rank;
	}

	public void onDeath() {
		if (laststreak > streak) {
			streak = laststreak;
		}

		laststreak = 0;
		deaths++;
	}

	@Override
	public String toString() {
		return "PlayerCache{" + "kills=" + kills + ", mob=" + mob + ", deaths=" + deaths + ", streak=" + streak + ", laststreak=" + laststreak + '}';
	}
}
