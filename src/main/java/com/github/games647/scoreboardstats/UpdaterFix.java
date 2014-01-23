package com.github.games647.scoreboardstats;

import java.io.File;

import net.gravitydevelopment.updater.Updater;

import org.bukkit.plugin.Plugin;

public class UpdaterFix extends Updater {

    public UpdaterFix(Plugin plugin, int id, File file) {
        super(plugin, id, file, Updater.UpdateType.DEFAULT, false);
    }

    @Override
    public boolean shouldUpdate(String localVersion, String remoteVersion) {
        //Fix curse api issue it's only a temp fix
        final String local = localVersion.replace(".", "");
        final String remote = remoteVersion.replace(".", "");
        return Integer.parseInt(remote) > Integer.parseInt(local);
    }
}
