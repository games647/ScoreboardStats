package com.github.games647.scoreboardstats.variables;

import com.github.games647.scoreboardstats.Version;

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
        Server server = Mockito.mock(Server.class);
        Mockito.when(Bukkit.getServer()).thenReturn(server);

        Mockito.when(Bukkit.getVersion()).thenReturn(DEFAULT_VERSION);

        //Plugin Parsing of FactionsUUID; shouldn't fail
        Version.parse("1.6.9.5-U0.1.12-SNAPSHOT");
    }

    /**
     * Test of compareTo method, of class Version.
     */
    @Test
    public void testComparison() {
        Version low = new Version(1, 5, 4);
        Version high = new Version(1, 8, 5);

        Assert.assertSame("Higher Compare: " + high + ' ' + low, high.compareTo(low), 1);
        Assert.assertSame("Lower Compare: " + low + ' ' + high, low.compareTo(high), -1);

        Version higher = new Version(1, 5, 5);
        Assert.assertSame("Higher Compare: " + higher + ' ' + low, higher.compareTo(low), 1);
        Assert.assertSame("Lower Compare: " + low + ' ' + higher, low.compareTo(higher), -1);

        Version equal = new Version(1, 2, 3);
        Version equal1 = new Version(1, 2, 3);
        Assert.assertSame("Equal Compare: " + equal + ' ' + equal1, equal.compareTo(equal1), 0);
        Assert.assertSame("Equal Compare: " + equal1 + ' ' + equal, equal1.compareTo(equal1), 0);
    }
}
