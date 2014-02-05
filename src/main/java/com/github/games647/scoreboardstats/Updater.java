/*
 * Updater for Bukkit.
 *
 * This class provides the means to safely and easily update a plugin, or check to see if it is updated using dev.bukkit.org
 */
package com.github.games647.scoreboardstats;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

public class Updater {

    private static final String TITLE_VALUE = "name"; // Gets remote file's title
    private static final String LINK_VALUE = "downloadUrl"; // Gets remote file's download link
    private static final String QUERY = "https://api.curseforge.com/servermods/files?projectIds="; // Slugs will be appended to this to get to the project's RSS feed

    private static final String USER_AGENT = "Updater (by Gravity and modifed by games647)";
    private static final String DELIMITER = "^v|[\\s_-]v"; // Used for locating version numbers in file names
    private static final String[] NO_UPDATE_TAG = { "-DEV", "-PRE", "-SNAPSHOT" }; // If the version number contains one of these, don't update.

    protected final Plugin plugin;
    protected final boolean shouldDownload;
    protected String versionName;
    protected String versionLink;

    protected final File file;
    protected URL url; // Connecting to RSS
    protected final String updateFolder;// The folder that downloads will be placed in
    protected Updater.UpdateResult result = Updater.UpdateResult.SUCCESS; // Used for determining the outcome of the update process

    private final Thread thread;
    private final int projectId; // Project's Curse ID

    public Updater(Plugin plugin, File file, int projectId, boolean shouldDownload) {
        this.plugin = plugin;
        this.shouldDownload = shouldDownload;
        this.file = file;
        this.projectId = projectId;
        this.updateFolder = plugin.getServer().getUpdateFolder();

        try {
            this.url = new URL(Updater.QUERY + projectId);
        } catch (MalformedURLException e) {
            //This can only happen if we do something wrong not the user
        }

        this.thread = new Thread(new UpdateRunnable());
        this.thread.start();
    }

    /**
     * Get the result of the update process.
     *
     * @return result of the update process.
     * @see UpdateResult
     */
    public Updater.UpdateResult getResult() {
        this.waitForThread();
        return this.result;
    }

    /**
     * Get the latest version's name (such as "Project v1.0").
     *
     * @return latest version's name.
     */
    public String getLatestName() {
        this.waitForThread();
        return this.versionName;
    }

    /**
     * <b>If you wish to run mathematical versioning checks, edit this method.</b>
     * <p>
     * With default behavior, Updater will NOT verify that a remote version available on BukkitDev
     * which is not this version is indeed an "update".
     * If a version is present on BukkitDev that is not the version that is currently running,
     * Updater will assume that it is a newer version.
     * This is because there is no standard versioning scheme, and creating a calculation that can
     * determine whether a new update is actually an update is sometimes extremely complicated.
     * </p>
     * <p>
     * Updater will call this method from {@link #versionCheck(String)} before deciding whether
     * the remote version is actually an update.
     * If you have a specific versioning scheme with which a mathematical determination can
     * be reliably made to decide whether one version is higher than another, you may
     * revise this method, using the local and remote version parameters, to execute the
     * appropriate check.
     * </p>
     * <p>
     * Returning a value of <b>false</b> will tell the update process that this is NOT a new version.
     * Without revision, this method will always consider a remote version at all different from
     * that of the local version a new update.
     * </p>
     * @param localVersion the current version
     * @param remoteVersion the remote version
     * @return true if Updater should consider the remote version an update, false if not.
     */
    public boolean shouldUpdate(String localVersion, String remoteVersion) {
        return !localVersion.equalsIgnoreCase(remoteVersion);
    }

    /**
     * As the result of Updater output depends on the thread's completion, it is necessary to wait for the thread to finish
     * before allowing anyone to check the result.
     */
    private void waitForThread() {
        if (this.thread != null && this.thread.isAlive()) {
            try {
                this.thread.join();
            } catch (InterruptedException e) {
                plugin.getLogger().log(Level.SEVERE, null, e);
            }
        }
    }

    /**
     * Save an update from dev.bukkit.org into the server's update folder.
     *
     * @param folder the updates folder location.
     * @param file the name of the file to save it as.
     * @param link the url of the file.
     */
    private void saveFile(File folder, String file, String link) {
        if (!folder.exists()) {
            folder.mkdir();
        }

        BufferedInputStream inputStream = null;
        FileOutputStream fout = null;
        try {
            // Download the file
            inputStream = new BufferedInputStream(new URL(link).openStream());
            fout = new FileOutputStream(folder.getAbsolutePath() + File.separator + file);

            ByteStreams.copy(inputStream, fout);
        } catch (Exception ex) {
            this.plugin.getLogger().warning("The auto-updater tried to download a new update, but was unsuccessful.");
            this.result = Updater.UpdateResult.FAIL_DOWNLOAD;
        } finally {
            Closeables.closeQuietly(inputStream);
            Closeables.closeQuietly(fout);
        }
    }

    /**
     * Check to see if the program should continue by evaluating whether the plugin is already updated, or shouldn't be updated.
     *
     * @param title the plugin's title.
     * @return true if the version was located and is not the same as the remote's newest.
     */
    private boolean versionCheck(String title) {
        final String localVersion = this.plugin.getDescription().getVersion();
        if (title.split(DELIMITER).length == 2) {
            final String remoteVersion = title.split(DELIMITER)[1].split(" ")[0]; // Get the newest file's version number

            if (this.hasTag(localVersion) || !this.shouldUpdate(localVersion, remoteVersion)) {
                // We already have the latest version, or this build is tagged for no-update
                this.result = Updater.UpdateResult.NO_UPDATE;
                return false;
            }
        } else {
            // The file's name did not contain the string 'vVersion'
            this.plugin.getLogger().warning("Wrong file name format. File versions should follow the format 'PluginName vVERSION'");
            this.result = Updater.UpdateResult.FAIL_NOVERSION;
            return false;
        }

        return true;
    }

    /**
     * Evaluate whether the version number is marked showing that it should not be updated by this program.
     *
     * @param version a version number to check for tags in.
     * @return true if updating should be disabled.
     */
    private boolean hasTag(String version) {
        for (String string : Updater.NO_UPDATE_TAG) {
            if (version.contains(string)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Make a connection to the BukkitDev API and request the newest file's details.
     *
     * @return true if successful.
     */
    private boolean read() {
        try {
            final URLConnection conn = this.url.openConnection();
            conn.setConnectTimeout(5000);

            conn.addRequestProperty("User-Agent", Updater.USER_AGENT);

            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final JSONArray array = (JSONArray) JSONValue.parse(reader.readLine());
            if (array.isEmpty()) {
                this.plugin.getLogger().log(Level.WARNING, "The updater could not find any files for the project id {0}", this.projectId);
                this.result = UpdateResult.FAIL_BADID;
                return false;
            }

            this.versionName = (String) ((Map) array.get(array.size() - 1)).get(Updater.TITLE_VALUE);
            this.versionLink = (String) ((Map) array.get(array.size() - 1)).get(Updater.LINK_VALUE);

            return true;
        } catch (IOException e) {
            this.plugin.getLogger().severe("The updater could not contact dev.bukkit.org for updating.");
            this.plugin.getLogger().severe("If you have not recently modified your configuration and this is the first time you are seeing this message, the site may be experiencing temporary downtime.");
            this.result = UpdateResult.FAIL_DBO;
            this.plugin.getLogger().log(Level.SEVERE, null, e);
            return false;
        }
    }

    /**
     * Gives the developer the result of the update process. Can be obtained by called {@link #getResult()}
     */
    public enum UpdateResult {
        /**
         * The updater found an update, and has readied it to be loaded the next time the server restarts/reloads.
         */
        SUCCESS,
        /**
         * The updater did not find an update, and nothing was downloaded.
         */
        NO_UPDATE,
        /**
         * The updater found an update, but was unable to download it.
         */
        FAIL_DOWNLOAD,
        /**
         * For some reason, the updater was unable to contact dev.bukkit.org to download the file.
         */
        FAIL_DBO,
        /**
         * When running the version check, the file on DBO did not contain a recognizable version.
         */
        FAIL_NOVERSION,
        /**
         * The id provided by the plugin running the updater was invalid and doesn't exist on DBO.
         */
        FAIL_BADID,
        /**
         * The updater found an update, but it wasn't downloaded.
         */
        UPDATE_AVAILABLE
    }

    private class UpdateRunnable implements Runnable {

        @Override
        public void run() {
            // Obtain the results of the project's file feed
            if (Updater.this.url != null && Updater.this.read() && Updater.this.versionCheck(Updater.this.versionName)) {
                final String versionLink = Updater.this.versionLink;
                if (Updater.this.shouldDownload && versionLink != null) {
                    final String name = Updater.this.file.getName();
                    final File updateFolder = new File(plugin.getDataFolder().getParent(), Updater.this.updateFolder);
                    Updater.this.saveFile(updateFolder, name, versionLink);
                } else {
                    Updater.this.result = UpdateResult.UPDATE_AVAILABLE;
                }
            }
        }
    }
}