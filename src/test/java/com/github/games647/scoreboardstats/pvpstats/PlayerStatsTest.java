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
public class PlayerStatsTest {

    public PlayerStatsTest() {
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
     * Test of getPlayername method, of class PlayerStats.
     */
    @Test
    public void testGetPlayername() {
        System.out.println("getPlayername");
        PlayerStats instance = new PlayerStats();
        String expResult = "";
        String result = instance.getPlayername();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPlayername method, of class PlayerStats.
     */
    @Test
    public void testSetPlayername() {
        System.out.println("setPlayername");
        String paramplayername = "";
        PlayerStats instance = new PlayerStats();
        instance.setPlayername(paramplayername);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getKills method, of class PlayerStats.
     */
    @Test
    public void testGetKills() {
        System.out.println("getKills");
        PlayerStats instance = new PlayerStats();
        int expResult = 0;
        int result = instance.getKills();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setKills method, of class PlayerStats.
     */
    @Test
    public void testSetKills() {
        System.out.println("setKills");
        int paramkills = 0;
        PlayerStats instance = new PlayerStats();
        instance.setKills(paramkills);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDeaths method, of class PlayerStats.
     */
    @Test
    public void testGetDeaths() {
        System.out.println("getDeaths");
        PlayerStats instance = new PlayerStats();
        int expResult = 0;
        int result = instance.getDeaths();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDeaths method, of class PlayerStats.
     */
    @Test
    public void testSetDeaths() {
        System.out.println("setDeaths");
        int paramdeaths = 0;
        PlayerStats instance = new PlayerStats();
        instance.setDeaths(paramdeaths);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMobkills method, of class PlayerStats.
     */
    @Test
    public void testGetMobkills() {
        System.out.println("getMobkills");
        PlayerStats instance = new PlayerStats();
        int expResult = 0;
        int result = instance.getMobkills();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setMobkills method, of class PlayerStats.
     */
    @Test
    public void testSetMobkills() {
        System.out.println("setMobkills");
        int parammobkills = 0;
        PlayerStats instance = new PlayerStats();
        instance.setMobkills(parammobkills);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getKillstreak method, of class PlayerStats.
     */
    @Test
    public void testGetKillstreak() {
        System.out.println("getKillstreak");
        PlayerStats instance = new PlayerStats();
        int expResult = 0;
        int result = instance.getKillstreak();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setKillstreak method, of class PlayerStats.
     */
    @Test
    public void testSetKillstreak() {
        System.out.println("setKillstreak");
        int paramkillstreak = 0;
        PlayerStats instance = new PlayerStats();
        instance.setKillstreak(paramkillstreak);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}