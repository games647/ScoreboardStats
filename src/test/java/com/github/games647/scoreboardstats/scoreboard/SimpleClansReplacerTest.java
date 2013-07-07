/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.games647.scoreboardstats.scoreboard;

import org.bukkit.entity.Player;
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
public class SimpleClansReplacerTest {

    public SimpleClansReplacerTest() {
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
     * Test of getSimpleClans1Value method, of class SimpleClansReplacer.
     */
    @Test
    public void testGetSimpleClans1Value() {
        System.out.println("getSimpleClans1Value");
        String key = "";
        Player player = null;
        int expResult = 0;
        int result = SimpleClansReplacer.getSimpleClans1Value(key, player);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSimpleClans2Value method, of class SimpleClansReplacer.
     */
    @Test
    public void testGetSimpleClans2Value() {
        System.out.println("getSimpleClans2Value");
        String key = "";
        Player player = null;
        int expResult = 0;
        int result = SimpleClansReplacer.getSimpleClans2Value(key, player);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}