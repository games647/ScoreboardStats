/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.games647.scoreboardstats.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
public class SidebarCommandTest {

    public SidebarCommandTest() {
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
     * Test of onCommand method, of class SidebarCommand.
     */
    @Test
    public void testOnCommand() {
        System.out.println("onCommand");
        CommandSender cs = null;
        Command cmd = null;
        String label = "";
        String[] args = null;
        SidebarCommand instance = new SidebarCommand();
        boolean expResult = false;
        boolean result = instance.onCommand(cs, cmd, label, args);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}