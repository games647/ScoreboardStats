package me.games647.gscoreboard;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.PersistenceException;
import me.games647.gscoreboard.api.Database;
import me.games647.gscoreboard.api.PlayerStats;
import me.games647.gscoreboard.listener.DeathListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class GScoreboard extends JavaPlugin {

    @Override
    public void onEnable() {
        setupDatabase();
        this.getServer().getPluginManager().registerEvents(new DeathListener(), this);
    }

    private void setupDatabase() {
        try {
            getDatabase().find(PlayerStats.class).findRowCount();
        } catch (PersistenceException ex) {
            installDDL();
        }
        Database.setDatabase(getDatabase());
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        final List<Class<?>> list = new ArrayList<Class<?>>(1);
        list.add(PlayerStats.class);
        return list;
    }

}
