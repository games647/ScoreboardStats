/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.games647.scoreboardstats.pvpstats;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author games647
 */
public class PlayerCacheTest {

    public PlayerCacheTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getKills method, of class PlayerCache.
     */
    @Test
    public void testGetKills() {
        System.out.println("getKills");
        PlayerCache instance = new PlayerCache();
        int expResult = 0;
        int result = instance.getKills();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMob method, of class PlayerCache.
     */
    @Test
    public void testGetMob() {
        System.out.println("getMob");
        PlayerCache instance = new PlayerCache();
        int expResult = 0;
        int result = instance.getMob();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDeaths method, of class PlayerCache.
     */
    @Test
    public void testGetDeaths() {
        System.out.println("getDeaths");
        PlayerCache instance = new PlayerCache();
        int expResult = 0;
        int result = instance.getDeaths();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStreak method, of class PlayerCache.
     */
    @Test
    public void testGetStreak() {
        System.out.println("getStreak");
        PlayerCache instance = new PlayerCache();
        int expResult = 0;
        int result = instance.getStreak();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onKill method, of class PlayerCache.
     */
    @Test
    public void testOnKill() {
        System.out.println("onKill");
        PlayerCache instance = new PlayerCache();
        instance.onKill();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of increaseMob method, of class PlayerCache.
     */
    @Test
    public void testIncreaseMob() {
        System.out.println("increaseMob");
        PlayerCache instance = new PlayerCache();
        instance.increaseMob();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLastStreak method, of class PlayerCache.
     */
    @Test
    public void testGetLastStreak() {
        System.out.println("getLastStreak");
        PlayerCache instance = new PlayerCache();
        int expResult = 0;
        int result = instance.getLastStreak();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onDeath method, of class PlayerCache.
     */
    @Test
    public void testOnDeath() {
        System.out.println("onDeath");
        PlayerCache instance = new PlayerCache();
        instance.onDeath();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class PlayerCache.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        PlayerCache instance = new PlayerCache();
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}