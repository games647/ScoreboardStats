package com.github.games647.scoreboardstats;

import java.io.File;


import org.bukkit.plugin.Plugin;

/*
 * Fix curse api issue due it's not updating so fast
 */
public class UpdaterFix extends Updater {

    public UpdaterFix(Plugin plugin, File file) {
        super(plugin, 55148, file, Updater.UpdateType.DEFAULT, false);
    }

    @Override
    public boolean shouldUpdate(String localVersion, String remoteVersion) {
        //Convert the version into integers
        final String local = localVersion.replace(".", "");
        final String remote = remoteVersion.replace(".", "");
        return Integer.parseInt(remote) > Integer.parseInt(local);
    }
}
