/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.games647.scoreboardstats;

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
public class SettingsTest {

    public SettingsTest() {
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
     * Test of loadConfig method, of class Settings.
     */
    @Test
    public void testLoadConfig() {
        System.out.println("loadConfig");
        Settings.loadConfig();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sendUpdate method, of class Settings.
     */
    @Test
    public void testSendUpdate() {
        System.out.println("sendUpdate");
        Player player = null;
        boolean complete = false;
        Settings.sendUpdate(player, complete);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isPvpStats method, of class Settings.
     */
    @Test
    public void testIsPvpStats() {
        System.out.println("isPvpStats");
        boolean expResult = false;
        boolean result = Settings.isPvpStats();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isTempScoreboard method, of class Settings.
     */
    @Test
    public void testIsTempScoreboard() {
        System.out.println("isTempScoreboard");
        boolean expResult = false;
        boolean result = Settings.isTempScoreboard();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isHideVanished method, of class Settings.
     */
    @Test
    public void testIsHideVanished() {
        System.out.println("isHideVanished");
        boolean expResult = false;
        boolean result = Settings.isHideVanished();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isSound method, of class Settings.
     */
    @Test
    public void testIsSound() {
        System.out.println("isSound");
        boolean expResult = false;
        boolean result = Settings.isSound();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isUpdateInfo method, of class Settings.
     */
    @Test
    public void testIsUpdateInfo() {
        System.out.println("isUpdateInfo");
        boolean expResult = false;
        boolean result = Settings.isUpdateInfo();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isPacketsystem method, of class Settings.
     */
    @Test
    public void testIsPacketsystem() {
        System.out.println("isPacketsystem");
        boolean expResult = false;
        boolean result = Settings.isPacketsystem();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTitle method, of class Settings.
     */
    @Test
    public void testGetTitle() {
        System.out.println("getTitle");
        String expResult = "";
        String result = Settings.getTitle();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTempTitle method, of class Settings.
     */
    @Test
    public void testGetTempTitle() {
        System.out.println("getTempTitle");
        String expResult = "";
        String result = Settings.getTempTitle();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTempColor method, of class Settings.
     */
    @Test
    public void testGetTempColor() {
        System.out.println("getTempColor");
        String expResult = "";
        String result = Settings.getTempColor();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTopType method, of class Settings.
     */
    @Test
    public void testGetTopType() {
        System.out.println("getTopType");
        String expResult = "";
        String result = Settings.getTopType();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIntervall method, of class Settings.
     */
    @Test
    public void testGetIntervall() {
        System.out.println("getIntervall");
        int expResult = 0;
        int result = Settings.getIntervall();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTopitems method, of class Settings.
     */
    @Test
    public void testGetTopitems() {
        System.out.println("getTopitems");
        int expResult = 0;
        int result = Settings.getTopitems();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTempShow method, of class Settings.
     */
    @Test
    public void testGetTempShow() {
        System.out.println("getTempShow");
        int expResult = 0;
        int result = Settings.getTempShow();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTempDisapper method, of class Settings.
     */
    @Test
    public void testGetTempDisapper() {
        System.out.println("getTempDisapper");
        int expResult = 0;
        int result = Settings.getTempDisapper();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getItemsLenght method, of class Settings.
     */
    @Test
    public void testGetItemsLenght() {
        System.out.println("getItemsLenght");
        int expResult = 0;
        int result = Settings.getItemsLenght();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isDisabledWorld method, of class Settings.
     */
    @Test
    public void testIsDisabledWorld() {
        System.out.println("isDisabledWorld");
        String name = "";
        boolean expResult = false;
        boolean result = Settings.isDisabledWorld(name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class Settings.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Settings instance = new Settings();
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}