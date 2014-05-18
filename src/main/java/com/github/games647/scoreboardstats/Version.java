package com.github.games647.scoreboardstats;

import com.google.common.collect.ComparisonChain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.bukkit.Bukkit;

/**
 *
 * @author games647
 */
@EqualsAndHashCode(doNotUseGetters = true)
@ToString(doNotUseGetters = true)
public class Version implements Comparable<Version> {

    //thanks to the author of ProtocolLib aadnk
    private static final String VERSION_REGEX = ".*\\(.*MC.\\s*([a-zA-z0-9\\-\\.]+)\\s*\\)";

    /**
     * Gets the minecraft version
     *
     * @return the minecraft version
     */
    public static Version getMinecraftVersion() {
        return new Version(getVersionStringFromServer(Bukkit.getVersion()));
    }

    private static String getVersionStringFromServer(String versionString) {
        final Pattern versionPattern = Pattern.compile(VERSION_REGEX);
        final Matcher version = versionPattern.matcher(versionString);

        if (version.matches() && version.group(1) != null) {
            return version.group(1);
        } else {
            throw new IllegalStateException("Cannot parse version String '" + versionString + '\'');
        }
    }

    private final int major;
    private final int minor;
    private final int build;

    /**
     * Creates a new version based on this string.
     *
     * @param version the version string
     * @throws IllegalArgumentException if the string doesn't match a version format
     */
    public Version(String version) throws IllegalArgumentException {
        //X.X.X
        if (!version.matches("\\d+\\.\\d+\\.\\d+")) {
            throw new IllegalArgumentException("Invalid format: " + version);
        }

        // escape regEx
        final String[] split = version.split("\\.");
        major = Integer.parseInt(split[0]);
        minor = Integer.parseInt(split[1]);
        build = Integer.parseInt(split[2]);
    }

    /**
     * Creates a new version based on these values.
     *
     * @param major the major version
     * @param minor the minor version
     * @param build the build version
     */
    public Version(int major, int minor, int build) {
        this.major = major;
        this.minor = minor;
        this.build = build;
    }

    /**
     * Gets the major value
     *
     * @return the major value
     */
    public int getMajor() {
        return major;
    }

    /**
     * Gets the minor value
     *
     * @return the minor value
     */
    public int getMinor() {
        return minor;
    }

    /**
     * Gets the build value
     *
     * @return the build value
     */
    public int getBuild() {
        return build;
    }

    @Override
    public int compareTo(Version other) {
        return ComparisonChain.start()
                .compare(major, other.major)
                .compare(minor, other.minor)
                .compare(build, other.build)
                .result();
    }
}
