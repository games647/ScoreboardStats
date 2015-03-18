package com.github.games647.scoreboardstats;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Version Test class
 *
 * @see Version
 */
@PrepareForTest(Bukkit.class)
@RunWith(PowerMockRunner.class)
public class VersionTest {

    private static final String DEFAULT_VERSION = "git-Bukkit-1.5.2-R1.0-1-gf46bd58-b2793jnks (MC: 1.7.9)";

    /**
     * Test version parsing
     */
    @Test
    public void testParsing() {
        //Bukkit version parsing. Can be found here: META-INF
        PowerMockito.mockStatic(Bukkit.class);
        final Server server = Mockito.mock(Server.class);
        Mockito.when(Bukkit.getServer()).thenReturn(server);

        Mockito.when(Bukkit.getVersion()).thenReturn(DEFAULT_VERSION);
        Version version = Version.getMinecraftVersion();

        Assert.assertEquals("Major version exception: " + version, 1, version.getMajor());
        Assert.assertEquals("Minor version exception: " + version, 7, version.getMinor());
        Assert.assertEquals("Build version exception: " + version, 9, version.getBuild());

        //Spigot parsing
        Mockito.when(Bukkit.getVersion()).thenReturn("git-Spigot-1439 (MC: 1.7.9)");
        version = Version.getMinecraftVersion();

        Assert.assertEquals("Major version exception: " + version, 1, version.getMajor());
        Assert.assertEquals("Minor version exception: " + version, 7, version.getMinor());
        Assert.assertEquals("Build version exception: " + version, 9, version.getBuild());

        //Glowstone
        Mockito.when(Bukkit.getVersion()).thenReturn("1.8-36-gbbc3960-dev");
        Mockito.when(server.toString()).thenReturn("GlowServer{name=" + "Glowstone"
                + ",version=" + "1.8-36-gbbc3960-dev" + ",minecraftVersion=" + "1.8" + '}');
        version = Version.getMinecraftVersion();

        Assert.assertEquals("Major version exception: " + version, 1, version.getMajor());

        //Plugin Parsing of FactionsUUID; shouldn't fail
        Version.parse("1.6.9.5-U0.1.12-SNAPSHOT");
    }

    /**
     * Test of compareTo method, of class Version.
     */
    @Test
    public void testComparison() {
        //Bukkit version parsing. Can be found here: META-INF
        PowerMockito.mockStatic(Bukkit.class);
        final Server server = Mockito.mock(Server.class);
        Mockito.when(Bukkit.getServer()).thenReturn(server);

        Mockito.when(Bukkit.getVersion()).thenReturn(DEFAULT_VERSION);

        final Version low = new Version(1, 5, 4);
        final Version high = new Version(1, 8, 5);

        Assert.assertSame("Higher Compare: " + high + ' ' + low, high.compareTo(low), 1);
        Assert.assertSame("Lower Compare: " + low + ' ' + high, low.compareTo(high), -1);

        final Version higher = new Version(1, 5, 5);
        Assert.assertSame("Higher Compare: " + higher + ' ' + low, higher.compareTo(low), 1);
        Assert.assertSame("Lower Compare: " + low + ' ' + higher, low.compareTo(higher), -1);

        final Version equal = new Version(1, 2, 3);
        final Version equal1 = new Version(1, 2, 3);
        Assert.assertSame("Equal Compare: " + equal + ' ' + equal1, equal.compareTo(equal1), 0);
        Assert.assertSame("Equal Compare: " + equal1 + ' ' + equal, equal1.compareTo(equal1), 0);
    }
}