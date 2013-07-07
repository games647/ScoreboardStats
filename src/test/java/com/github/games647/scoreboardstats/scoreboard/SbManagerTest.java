/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.games647.scoreboardstats.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
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
public class SbManagerTest {

    public SbManagerTest() {
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
     * Test of createScoreboard method, of class SbManager.
     */
    @Test
    public void testCreateScoreboard() {
        System.out.println("createScoreboard");
        Player player = null;
        SbManager.createScoreboard(player);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createTopListScoreboard method, of class SbManager.
     */
    @Test
    public void testCreateTopListScoreboard() {
        System.out.println("createTopListScoreboard");
        Player player = null;
        SbManager.createTopListScoreboard(player);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sendScore method, of class SbManager.
     */
    @Test
    public void testSendScore() {
        System.out.println("sendScore");
        Objective objective = null;
        String title = "";
        int value = 0;
        boolean complete = false;
        SbManager.sendScore(objective, title, value, complete);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of regAll method, of class SbManager.
     */
    @Test
    public void testRegAll() {
        System.out.println("regAll");
        SbManager.regAll();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of unregisterAll method, of class SbManager.
     */
    @Test
    public void testUnregisterAll() {
        System.out.println("unregisterAll");
        SbManager.unregisterAll();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}