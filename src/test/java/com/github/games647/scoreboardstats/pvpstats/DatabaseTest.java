/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.games647.scoreboardstats.pvpstats;

import com.avaje.ebean.EbeanServer;
import java.util.Map;
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
public class DatabaseTest {

    public DatabaseTest() {
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
     * Test of setDatabase method, of class Database.
     */
    @Test
    public void testSetDatabase() {
        System.out.println("setDatabase");
        EbeanServer base = null;
        Database.setDatabase(base);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCache method, of class Database.
     */
    @Test
    public void testGetCache() {
        System.out.println("getCache");
        String name = "";
        PlayerCache expResult = null;
        PlayerCache result = Database.getCache(name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of loadAccount method, of class Database.
     */
    @Test
    public void testLoadAccount() {
        System.out.println("loadAccount");
        String name = "";
        Database.loadAccount(name);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getKdr method, of class Database.
     */
    @Test
    public void testGetKdr() {
        System.out.println("getKdr");
        String name = "";
        int expResult = 0;
        int result = Database.getKdr(name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of saveAccount method, of class Database.
     */
    @Test
    public void testSaveAccount() {
        System.out.println("saveAccount");
        String name = "";
        boolean remove = false;
        Database.saveAccount(name, remove);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of saveAll method, of class Database.
     */
    @Test
    public void testSaveAll() {
        System.out.println("saveAll");
        Database.saveAll();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTop method, of class Database.
     */
    @Test
    public void testGetTop() {
        System.out.println("getTop");
        Map expResult = null;
        Map result = Database.getTop();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}