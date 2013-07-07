/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.games647.scoreboardstats.scoreboard;

import org.bukkit.scoreboard.DisplaySlot;
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
public class SObjectiveTest {

    public SObjectiveTest() {
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
     * Test of getDisplayslot method, of class SObjective.
     */
    @Test
    public void testGetDisplayslot() {
        System.out.println("getDisplayslot");
        SObjective instance = null;
        DisplaySlot expResult = null;
        DisplaySlot result = instance.getDisplayslot();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDisplayslot method, of class SObjective.
     */
    @Test
    public void testSetDisplayslot() {
        System.out.println("setDisplayslot");
        DisplaySlot displayslot = null;
        SObjective instance = null;
        instance.setDisplayslot(displayslot);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getObjectivename method, of class SObjective.
     */
    @Test
    public void testGetObjectivename() {
        System.out.println("getObjectivename");
        SObjective instance = null;
        String expResult = "";
        String result = instance.getObjectivename();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDisplayname method, of class SObjective.
     */
    @Test
    public void testGetDisplayname() {
        System.out.println("getDisplayname");
        SObjective instance = null;
        String expResult = "";
        String result = instance.getDisplayname();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isDisabled method, of class SObjective.
     */
    @Test
    public void testIsDisabled() {
        System.out.println("isDisabled");
        SObjective instance = null;
        boolean expResult = false;
        boolean result = instance.isDisabled();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDisplayname method, of class SObjective.
     */
    @Test
    public void testSetDisplayname() {
        System.out.println("setDisplayname");
        String displayname = "";
        SObjective instance = null;
        instance.setDisplayname(displayname);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDisabled method, of class SObjective.
     */
    @Test
    public void testSetDisabled() {
        System.out.println("setDisabled");
        SObjective instance = null;
        instance.setDisabled();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setEnabled method, of class SObjective.
     */
    @Test
    public void testSetEnabled() {
        System.out.println("setEnabled");
        SObjective instance = null;
        instance.setEnabled();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of update method, of class SObjective.
     */
    @Test
    public void testUpdate() {
        System.out.println("update");
        SObjective instance = null;
        instance.update();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}