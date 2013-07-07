/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.games647.scoreboardstats;

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
public class UpdaterTest {

    public UpdaterTest() {
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
     * Test of getResult method, of class Updater.
     */
    @Test
    public void testGetResult() {
        System.out.println("getResult");
        Updater instance = null;
        Updater.UpdateResult expResult = null;
        Updater.UpdateResult result = instance.getResult();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFileSize method, of class Updater.
     */
    @Test
    public void testGetFileSize() {
        System.out.println("getFileSize");
        Updater instance = null;
        long expResult = 0L;
        long result = instance.getFileSize();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLatestVersionString method, of class Updater.
     */
    @Test
    public void testGetLatestVersionString() {
        System.out.println("getLatestVersionString");
        Updater instance = null;
        String expResult = "";
        String result = instance.getLatestVersionString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of waitForThread method, of class Updater.
     */
    @Test
    public void testWaitForThread() {
        System.out.println("waitForThread");
        Updater instance = null;
        instance.waitForThread();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of pluginFile method, of class Updater.
     */
    @Test
    public void testPluginFile() {
        System.out.println("pluginFile");
        String name = "";
        Updater instance = null;
        boolean expResult = false;
        boolean result = instance.pluginFile(name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}