package com.github.games647.scoreboardstats;

import java.io.File;

import org.bukkit.plugin.Plugin;

/**
 * This file exists to make sure auto updater doesn't check for new updates if
 * the user has a development version
 *
 * @see Updater
 */
public class UpdaterFix extends Updater {

    public UpdaterFix(Plugin plugin, File file, boolean shouldDownload) {
        super(plugin, 55148, file, shouldDownload);
    }

    public UpdaterFix(Plugin plugin, File file, boolean shouldDownload, UpdateCallback callback) {
        super(plugin, 55148, file, shouldDownload, callback);
    }

    @Override
    public boolean shouldUpdate(String localVersion, String remoteVersion) {
        return Version.compare(localVersion, remoteVersion) > 0;
    }
}
