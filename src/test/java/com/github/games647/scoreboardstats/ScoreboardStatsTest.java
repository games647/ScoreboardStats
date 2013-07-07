/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.games647.scoreboardstats;

import java.util.List;
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
public class ScoreboardStatsTest {

    public ScoreboardStatsTest() {
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
     * Test of getInstance method, of class ScoreboardStats.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        ScoreboardStats expResult = null;
        ScoreboardStats result = ScoreboardStats.getInstance();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onEnable method, of class ScoreboardStats.
     */
    @Test
    public void testOnEnable() {
        System.out.println("onEnable");
        ScoreboardStats instance = new ScoreboardStats();
        instance.onEnable();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDatabaseClasses method, of class ScoreboardStats.
     */
    @Test
    public void testGetDatabaseClasses() {
        System.out.println("getDatabaseClasses");
        ScoreboardStats instance = new ScoreboardStats();
        List expResult = null;
        List result = instance.getDatabaseClasses();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onReload method, of class ScoreboardStats.
     */
    @Test
    public void testOnReload() {
        System.out.println("onReload");
        ScoreboardStats instance = new ScoreboardStats();
        instance.onReload();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onDisable method, of class ScoreboardStats.
     */
    @Test
    public void testOnDisable() {
        System.out.println("onDisable");
        ScoreboardStats instance = new ScoreboardStats();
        instance.onDisable();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}