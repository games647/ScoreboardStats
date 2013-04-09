package me.games647.scoreboardstats.settings;

import me.games647.scoreboardstats.ScoreboardStats;

public final class SettingsHandler {

    private final ScoreboardStats plugin;
    private boolean pvpstats;
    private String title;
    private java.util.Map<String, Object> items;
    private java.util.List<String> disabledworlds;

    public SettingsHandler(final ScoreboardStats instance) {
        this.plugin = instance;
        instance.saveDefaultConfig();
        loadConfig();
    }

    private void loadConfig() {
        final org.bukkit.configuration.file.FileConfiguration config = this.plugin.getConfig();
        this.pvpstats = config.getBoolean("enable-pvpstats");
        this.title = org.bukkit.ChatColor.translateAlternateColorCodes('&', config.getString("Scoreboard.Title"));
        items = config.getConfigurationSection("Scoreboard.Items").getValues(false);
        this.disabledworlds = config.getStringList("disabled-worlds");
        int delay = config.getInt("Scoreboard.Update-delay");
        if (delay <= 0) {
            delay = 1;
        }
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new me.games647.scoreboardstats.api.UpdateThread(), 60L, delay * 20L);
    }

    public String getTitle() {
        return title;
    }

    public boolean isPvpstats() {
        return pvpstats;
    }

    public boolean checkWorld(final String world) {
        return disabledworlds.contains(world);
    }

    public void sendUpdate(final org.bukkit.entity.Player player) {
        for (String localtitle : items.keySet()) {
            me.games647.scoreboardstats.api.Score.sendScore((
                    (org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer) player).getHandle().playerConnection
                    , localtitle
                    , me.games647.scoreboardstats.api.VariableReplacer.getValue((String) items.get(localtitle), player));
        }
    }
}
