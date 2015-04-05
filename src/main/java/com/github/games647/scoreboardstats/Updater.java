package com.github.games647.scoreboardstats;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * This is a simpler and optimized version of Gravity's Updater
 * Thanks for the great work
 *
 * You can find his project here: https://github.com/gravitylow/Updater/
 *
 * @author Gravity, games647
 * @version 2.2
 */
public class Updater {

    /* Constants */

    // Remote file's title
    private static final String TITLE_VALUE = "name";
    // Remote file's download link
    private static final String LINK_VALUE = "downloadUrl";
    // Path to GET
    private static final String QUERY = "https://api.curseforge.com/servermods/files?projectIds=";
    // Used for locating version numbers in file names
    private static final String DELIMETER = "^v|[\\s_-]v";
    // If the version number contains one of these, don't update.
    private static final String[] NO_UPDATE_TAG = {"-DEV", "-PRE", "-SNAPSHOT"};

    /* User-provided variables */

    // plugin running Updater
    private final Plugin plugin;
    // Type of update check to run
    private final boolean shouldDownload;
    // The plugin file (jar)
    private final File file;
    // The folder that downloads will be placed in
    private final File updateFolder;
    // The provided callback (if any)
    private final UpdateCallback callback;
    // Project's Curse ID
    private int id = -1;

    /* Collected from Curse API */

    private String versionName;
    private String versionLink;

    /* Update process variables */

    // Connection to RSS
    private URL url;
    // Updater thread
    private Thread thread;
    // Used for determining the outcome of the update process
    private Updater.UpdateResult result = Updater.UpdateResult.SUCCESS;

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
         * The updater found an update, but because of the shouldDownload being set to false, it wasn't downloaded.
         */
        UPDATE_AVAILABLE
    }

    /**
     * Initialize the updater.
     *
     * @param plugin    The plugin that is checking for an update.
     * @param id        The dev.bukkit.org id of the project.
     * @param file      The file that the plugin is running from, get this from getFile() from within your main class.
     * @param download  Specify the type of update this will be.
     */
    public Updater(Plugin plugin, int id, File file, boolean download) {
        this(plugin, id, file, download, null);
    }

    /**
     * Initialize the updater with the provided callback.
     *
     * @param plugin    The plugin that is checking for an update.
     * @param id        The dev.bukkit.org id of the project.
     * @param file      The file that the plugin is running from, get this from getFile() from within your main class.
     * @param download  Specify the type of update this will be.
     * @param callback  The callback instance to notify when the Updater has finished
     */
    public Updater(Plugin plugin, int id, File file, boolean download, UpdateCallback callback) {
        this.plugin = plugin;
        this.shouldDownload = download;
        this.file = file;
        this.id = id;
        this.updateFolder = this.plugin.getServer().getUpdateFolderFile();
        this.callback = callback;

        try {
            this.url = new URL(Updater.QUERY + this.id);
        } catch (MalformedURLException malformedURLEx) {
            //This can only happend if we modified the url. Just an int cannot make it malformed
            this.plugin.getLogger().log(Level.SEVERE, "Invalid URL", malformedURLEx);
            this.result = UpdateResult.FAIL_BADID;
            //cancel
            return;
        }

        this.thread = new Thread(new Runnable() {

            @Override
            public void run() {
                runUpdater();
            }
            //Recognize our updater
        }, plugin.getName() + "-Updater");

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
     * Get the latest version's direct file link.
     *
     * @return latest version's file link.
     */
    public String getLatestFileLink() {
        this.waitForThread();
        return this.versionLink;
    }

    /**
     * As the result of Updater output depends on the thread's completion, it is necessary
     * to wait for the thread to finish before allowing anyone to check the result.
     */
    private void waitForThread() {
        if (this.thread != null && this.thread.isAlive()) {
            try {
                this.thread.join();
            } catch (final InterruptedException e) {
                this.plugin.getLogger().log(Level.SEVERE, null, e);
            }
        }
    }

    /**
     * Save an update from dev.bukkit.org into the server's update folder.
     */
    private void saveFile() {
        final File folder = new File(plugin.getDataFolder().getParent(), plugin.getServer().getUpdateFolder());
        if (!folder.exists() && !folder.mkdir()) {
            plugin.getLogger().warning("Couldn't create update folder");
            return;
        }

        downloadFile();
    }

    /**
     * Download a file and save it to the specified folder.
     */
    private void downloadFile() {
        BufferedInputStream inputstream = null;
        FileOutputStream fout = null;
        try {
            final URL fileUrl = new URL(this.versionLink);
            inputstream = new BufferedInputStream(fileUrl.openStream());
            fout = new FileOutputStream(new File(this.updateFolder, file.getName()));

            ByteStreams.copy(inputstream, fout);
        } catch (Exception ex) {
            this.plugin.getLogger().log(Level.WARNING
                    , "The auto-updater tried to download a new update, but was unsuccessful.", ex);
            this.result = Updater.UpdateResult.FAIL_DOWNLOAD;
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException ex) {
                    this.plugin.getLogger().log(Level.SEVERE, null, ex);
                }
            }

            if (inputstream != null) {
                try {
                    inputstream.close();
                } catch (IOException ex) {
                    this.plugin.getLogger().log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Check to see if the program should continue by evaluating whether the plugin is already updated, or shouldn't be
     * updated.
     *
     * @return true if the version was located and is not the same as the remote's newest.
     */
    private boolean versionCheck() {
        final String title = this.versionName;
        final String localVersion = this.plugin.getDescription().getVersion();
        final String[] split = title.split(DELIMETER);
        if (split.length == 2) {
            // Get the newest file's version number
            final String remoteVersion = split[1].split(" ")[0];

            if (this.hasTag(localVersion) || !this.shouldUpdate(localVersion, remoteVersion)) {
                // We already have the latest version, or this build is tagged for no-update
                this.result = Updater.UpdateResult.NO_UPDATE;
                return false;
            }
        } else {
            // The file's name did not contain the string 'vVersion'
            this.plugin.getLogger().warning("File versions should follow the format 'PluginName vVERSION'");
            this.plugin.getLogger().warning("Please notify the author of this error.");
            this.result = Updater.UpdateResult.FAIL_NOVERSION;
            return false;
        }

        return true;
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
     * Updater will call this method from {@link #versionCheck()} before deciding whether
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
        if (this.url == null) {
            return false;
        }

        try {
            final URLConnection conn = this.url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);

            final String userAgent = String.format("%s/v%s (by %s)"
                    , plugin.getName()
                    , plugin.getDescription().getVersion()
                    , plugin.getDescription().getAuthors());
            conn.addRequestProperty("User-Agent", userAgent);

            final InputStreamReader streamReader = new InputStreamReader(conn.getInputStream(), Charsets.UTF_8);

            final JSONArray array = (JSONArray) JSONValue.parse(streamReader);
            if (array.isEmpty()) {
                this.plugin.getLogger().log(Level.WARNING
                        , "The updater could not find any file for the project id: {0}", this.id);
                this.result = UpdateResult.FAIL_BADID;
                return false;
            }

            //gets the last entry
            final JSONObject latestUpdate = (JSONObject) array.get(array.size() - 1);
            this.versionName = (String) latestUpdate.get(Updater.TITLE_VALUE);
            this.versionLink = (String) latestUpdate.get(Updater.LINK_VALUE);

            return true;
        } catch (IOException e) {
            this.plugin.getLogger().severe("The updater could not contact dev.bukkit.org for updating.");
            this.plugin.getLogger().severe("The site may be experiencing temporary downtime.");
            this.plugin.getLogger().log(Level.SEVERE, null, e);
            this.result = UpdateResult.FAIL_DBO;
            return false;
        }
    }

    /**
     * Called on main thread when the Updater has finished working, regardless
     * of result.
     */
    public interface UpdateCallback {
        /**
         * Called when the updater has finished working.
         * @param updater The updater instance
         */
        void onFinish(Updater updater);
    }

    private void runUpdater() {
        if (this.read() && this.versionCheck()) {
            // Obtain the results of the project's file feed
            if (this.versionLink != null && this.shouldDownload) {
                this.saveFile();
            } else {
                this.result = UpdateResult.UPDATE_AVAILABLE;
            }
        }

        if (this.callback != null) {
            this.plugin.getServer().getScheduler().runTask(plugin, new Runnable() {

                @Override
                public void run() {
                    runCallback();
                }
            });
        }
    }

    private void runCallback() {
        this.callback.onFinish(this);
    }
}
