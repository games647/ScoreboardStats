package com.github.games647.scoreboardstats;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

/**
 * Modified version of Gravity's Updater with some improvements
 *
 * Thanks to Gravity's work. You can find his project here: https://github.com/gravitylow/Updater/
 */
public class Updater {

    //Gets remote file's title
    private static final String TITLE_VALUE = "name";
    //Gets remote file's download link
    private static final String LINK_VALUE = "downloadUrl";
    //Gets remote file's release type
    private static final String TYPE_VALUE = "releaseType";
    //Slugs will be appended to this to get to the project's RSS feed
    private static final String QUERY = "https://api.curseforge.com/servermods/files?projectIds=";
    //Used for locating version numbers in file names
    private static final String DELIMITER = "^v|[\\s_-]v";

    protected final Plugin plugin;
    protected final boolean shouldDownload;
    protected String versionName;
    protected String versionLink;
    protected String versionType;

    protected final String fileName;
    //Connecting to RSS
    protected URL url;
    //The folder that downloads will be placed in
    protected final String updateFolder;
    //Used for determining the outcome of the update process
    protected Updater.UpdateResult result = Updater.UpdateResult.SUCCESS;

    private final Thread thread;
    //Project's Curse ID
    private final int projectId;

    /**
     * Creates a new updater instance that checks for an update
     *
     * @param plugin the plugin what should be checked for an update
     * @param file the jar file of the plugin
     * @param projectId the project id on curse
     * @param shouldDownload whether the updater should download the upade
     */
    public Updater(Plugin plugin, File file, int projectId, boolean shouldDownload) {
        this.plugin = plugin;
        this.fileName = file.getName();
        this.projectId = projectId;
        this.shouldDownload = shouldDownload;

        this.updateFolder = plugin.getServer().getUpdateFolder();

        try {
            this.url = new URL(Updater.QUERY + projectId);
        } catch (MalformedURLException e) {
            //This can only happen if we do something wrong not the user
            throw new RuntimeException(e);
        }

        this.thread = new Thread(new UpdateRunnable());
        this.thread.setName(plugin.getName() + "-Update Checker");
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
        if (this.thread.isAlive()) {
            try {
                this.thread.join();
            } catch (InterruptedException ex) {
                plugin.getLogger().log(Level.SEVERE, null, ex);
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
    private void saveFile(String link) {
        final File folder = new File(plugin.getDataFolder().getParent(), Updater.this.updateFolder);
        if (!folder.exists()) {
            folder.mkdir();
        }

        InputStream inputStream = null;
        FileOutputStream fout = null;
        try {
            // Download the file
            inputStream = new URL(link).openStream();
            fout = new FileOutputStream(folder.getAbsolutePath() + File.separator + this.fileName);

            ByteStreams.copy(inputStream, fout);
        } catch (IOException ex) {
            this.plugin.getLogger().log(Level.WARNING, "The auto-updater tried to download a new update, but was unsuccessful.", ex);
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
        final String[] removeVersions = title.split(DELIMITER);
        if (removeVersions.length == 2) {
            // Get the newest file's version number
            final String remoteVersion = removeVersions[1].split(" ")[0];

            if ("release".equals(this.versionType) || !this.shouldUpdate(localVersion, remoteVersion)) {
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
     * Make a connection to the BukkitDev API and request the newest file's details.
     *
     * @return true if successful.
     */
    private boolean read() {
        BufferedReader reader = null;
        try {
            final URLConnection conn = this.url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);

            final String userAgent = String.format("%s/v%s (by %s)"
                    , plugin.getName()
                    , plugin.getDescription().getVersion()
                    , plugin.getDescription().getAuthors().toString());
            conn.addRequestProperty("User-Agent", userAgent);

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charsets.UTF_8));
            final JSONArray array = (JSONArray) JSONValue.parse(reader);
            if (array.isEmpty()) {
                this.plugin.getLogger().log(Level.WARNING, "The updater could not find any files for the project id {0}", this.projectId);
                this.result = UpdateResult.FAIL_BADID;
                return false;
            }

            final Map<String, String> lastEntry = (Map<String, String>) array.get(array.size() - 1);

            this.versionName = lastEntry.get(Updater.TITLE_VALUE);
            this.versionLink = lastEntry.get(Updater.LINK_VALUE);
            this.versionType = lastEntry.get(Updater.TYPE_VALUE);

            return true;
        } catch (IOException ex) {
            this.result = UpdateResult.FAIL_DBO;

            this.plugin.getLogger().log(Level.SEVERE, "The updater could not contact dev.bukkit.org to check for an update.");
            this.plugin.getLogger().log(Level.FINE, null, ex);

            return false;
        } finally {
            Closeables.closeQuietly(reader);
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
                final String link = Updater.this.versionLink;
                if (link != null && Updater.this.shouldDownload) {
                    Updater.this.saveFile(link);
                } else {
                    Updater.this.result = UpdateResult.UPDATE_AVAILABLE;
                }
            }
        }
    }
}