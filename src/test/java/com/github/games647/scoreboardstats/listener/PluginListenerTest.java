/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.games647.scoreboardstats.listener;

import com.earth2me.essentials.EssentialsTimer;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.p000ison.dev.simpleclans2.clanplayer.CraftClanPlayerManager;
import net.milkbowl.vault.economy.Economy;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
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
public class PluginListenerTest {

    public PluginListenerTest() {
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
     * Test of getEconomy method, of class PluginListener.
     */
    @Test
    public void testGetEconomy() {
        System.out.println("getEconomy");
        Economy expResult = null;
        Economy result = PluginListener.getEconomy();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isMcmmo method, of class PluginListener.
     */
    @Test
    public void testIsMcmmo() {
        System.out.println("isMcmmo");
        boolean expResult = false;
        boolean result = PluginListener.isMcmmo();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSimpleclans method, of class PluginListener.
     */
    @Test
    public void testGetSimpleclans() {
        System.out.println("getSimpleclans");
        CraftClanPlayerManager expResult = null;
        CraftClanPlayerManager result = PluginListener.getSimpleclans();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEssentials method, of class PluginListener.
     */
    @Test
    public void testGetEssentials() {
        System.out.println("getEssentials");
        EssentialsTimer expResult = null;
        EssentialsTimer result = PluginListener.getEssentials();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHeroes method, of class PluginListener.
     */
    @Test
    public void testGetHeroes() {
        System.out.println("getHeroes");
        CharacterManager expResult = null;
        CharacterManager result = PluginListener.getHeroes();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFactions method, of class PluginListener.
     */
    @Test
    public void testGetFactions() {
        System.out.println("getFactions");
        String expResult = "";
        String result = PluginListener.getFactions();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSimpleclans2 method, of class PluginListener.
     */
    @Test
    public void testGetSimpleclans2() {
        System.out.println("getSimpleclans2");
        ClanManager expResult = null;
        ClanManager result = PluginListener.getSimpleclans2();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of init method, of class PluginListener.
     */
    @Test
    public void testInit() {
        System.out.println("init");
        PluginListener.init();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}