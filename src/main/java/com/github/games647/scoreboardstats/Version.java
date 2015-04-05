package com.github.games647.scoreboardstats;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.bukkit.Bukkit;

/**
 * Version class for comparing and detecting Minecraft and other versions
 */
public class Version implements Comparable<Version> {

    //thanks to the author of ProtocolLib aadnk
    private static final String VERSION_REGEX = ".*\\(.*MC.\\s*([a-zA-z0-9\\-\\.]+)\\s*\\)";

    private static boolean UUID_COMPATIBLE;

    static {
        try {
            UUID_COMPATIBLE = compare("1.7", getMinecraftVersionString()) >= 0;
        } catch (Exception ex) {
            UUID_COMPATIBLE = false;
        }
    }

    /**
     * Gets whether server supports UUIDs
     *
     * @return whether server supports UUIDs
     */
    public static boolean isUUIDCompatible() {
        return UUID_COMPATIBLE;
    }

    /**
     * Gets the Minecraft version
     *
     * @return the Minecraft version
     */
    public static Version getMinecraftVersion() {
        return new Version(getMinecraftVersionString());
    }

    /**
     * Gets the Minecraft version as string
     *
     * @return the Minecraft version as string
     */
    public static String getMinecraftVersionString() {
        return getVersionStringFromServer(Bukkit.getVersion());
    }

    /**
     * Compares the version with checking the first three numbers
     *
     * @param expected the object to be compared.
     * @param version the object to be compared.
     * @return 1 version is higher; 0 both are equal; -1 version is lower<br>
     *          a negative integer, zero, or a positive integer as this object
     *          is less than, equal to, or greater than the specified object.
     */
    public static int compare(String expected, String version) {
        final int[] expectedParts = parse(version);
        final int[] versionParts = parse(expected);

        return ComparisonChain.start()
                .compare(expectedParts[0], versionParts[0])
                .compare(expectedParts[1], versionParts[1])
                .compare(expectedParts[2], versionParts[2])
                .result();
    }

    /**
     * Separate the version into major, minor, build integers
     *
     * @param version the version that should be parsed
     * @return the version parts
     * @throws IllegalArgumentException if the version doesn't contains only positive numbers separated by max. 5 dots.
     */
    public static int[] parse(String version) throws IllegalArgumentException {
        //exludes spaces which could be added by mistake and exclude build suffixes
        final String trimedVersion = version.trim().split("\\-")[0];
        if (!trimedVersion.matches("\\d+(\\.\\d+){0,5}")) {
            //check if it's a format like '1.5'
            throw new IllegalArgumentException("Invalid format: " + version);
        }

        final int[] versionParts = new int[3];

        //escape regEx and split by dots
        final String[] split = trimedVersion.split("\\.");
        //We check if the length has min 1 entry.
        for (int i = 0; i < split.length && i < versionParts.length; i++) {
            versionParts[i] = Integer.parseInt(split[i]);
        }

        return versionParts;
    }

    private static String getVersionStringFromServer(String versionString) {
        final Pattern versionPattern = Pattern.compile(VERSION_REGEX);
        final Matcher versionMatche = versionPattern.matcher(versionString);

        if (versionMatche.matches() && versionMatche.group(1) != null) {
            return versionMatche.group(1);
        } else {
            //fallback to the toString() method
            final String[] split = Bukkit.getServer().toString().split("[,}]");
            for (String element : split) {
                if (element.contains("minecraftVersion=")) {
                    return element.split("minecraftVersion=")[1];
                }
            }

            //Couldn't extract the version
            throw new IllegalStateException("Cannot parse version String '" + versionString);
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
        final int[] versionParts = parse(version);

        major = versionParts[0];
        minor = versionParts[1];
        build = versionParts[2];
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

    @Override
    public int hashCode() {
        return Objects.hashCode(major, minor, build);
    }

    @Override
    public boolean equals(Object obj) {
        //ignores also null
        if (obj instanceof Version) {
            final Version other = (Version) obj;
            return major == other.major
                    && minor == other.minor
                    && build == other.build;
        }

        return false;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
