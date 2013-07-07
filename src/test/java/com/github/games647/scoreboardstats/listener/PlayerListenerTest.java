/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.games647.scoreboardstats.listener;

import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
public class PlayerListenerTest {

    public PlayerListenerTest() {
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
     * Test of onDeath method, of class PlayerListener.
     */
    @Test
    public void testOnDeath() {
        System.out.println("onDeath");
        PlayerDeathEvent death = null;
        PlayerListener.onDeath(death);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onJoin method, of class PlayerListener.
     */
    @Test
    public void testOnJoin() {
        System.out.println("onJoin");
        PlayerJoinEvent join = null;
        PlayerListener.onJoin(join);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onChange method, of class PlayerListener.
     */
    @Test
    public void testOnChange() {
        System.out.println("onChange");
        PlayerChangedWorldEvent teleport = null;
        PlayerListener.onChange(teleport);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onKick method, of class PlayerListener.
     */
    @Test
    public void testOnKick() {
        System.out.println("onKick");
        PlayerKickEvent kick = null;
        PlayerListener.onKick(kick);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onQuit method, of class PlayerListener.
     */
    @Test
    public void testOnQuit() {
        System.out.println("onQuit");
        PlayerQuitEvent quit = null;
        PlayerListener.onQuit(quit);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}