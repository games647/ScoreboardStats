package com.github.games647.scoreboardstats;

import java.io.File;

import org.bukkit.plugin.Plugin;

/**
 * This file exists to make sure auto updater doesn't check for new updates if
 * the user has a development version.
 *
 * @see Updater
 */
public class UpdaterFix extends Updater {

    private static final int CURSE_PROJECTID = 55148;

    /**
     * Initialize the updater.
     *
     * @param plugin    The plugin that is checking for an update.
     * @param file      The file that the plugin is running from, get this from getFile() from within your main class.
     * @param download  Specify the type of update this will be.
     */
    public UpdaterFix(Plugin plugin, File file, boolean download) {
        this(plugin, file, download, null);
    }

    /**
     * Initialize the updater with the provided callback.
     *
     * @param plugin    The plugin that is checking for an update.
     * @param file      The file that the plugin is running from, get this from getFile() from within your main class.
     * @param download  Specify the type of update this will be.
     * @param callback  The callback instance to notify when the Updater has finished
     */
    public UpdaterFix(Plugin plugin, File file, boolean download, UpdateCallback callback) {
        super(plugin, CURSE_PROJECTID, file, download, callback);
    }

    @Override
    public boolean shouldUpdate(String localVersion, String remoteVersion) {
        //return true if remoteVersion is higher
        return Version.compare(localVersion, remoteVersion) > 0;
    }
}