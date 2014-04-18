package com.github.games647.scoreboardstats.variables;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Tests for the replace manager
 */
public class ReplaceManagerTest {

    /**
     * Test of compare method, of class ReplaceManager.
     */
    @Test
    public void testCompare() {
        final String low = "1.5.4";
        final String high = "1.8.5.6";

        assertEquals(ReplaceManager.compare(low, high), 1);
        assertEquals(ReplaceManager.compare(high, low), -1);

        final String low1 = "1.6";
        final String high1 = "1.6.1";
        assertEquals(ReplaceManager.compare(low1, high1), 1);

        final String equal = "1.5";
        assertEquals(ReplaceManager.compare(equal, equal), 0);
    }
}
