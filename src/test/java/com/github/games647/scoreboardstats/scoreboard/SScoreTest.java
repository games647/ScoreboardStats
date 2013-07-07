/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.games647.scoreboardstats.scoreboard;

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
public class SScoreTest {

    public SScoreTest() {
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
     * Test of getDisplayname method, of class SScore.
     */
    @Test
    public void testGetDisplayname() {
        System.out.println("getDisplayname");
        SScore instance = null;
        String expResult = "";
        String result = instance.getDisplayname();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDisplayname method, of class SScore.
     */
    @Test
    public void testSetDisplayname() {
        System.out.println("setDisplayname");
        String displayname = "";
        SScore instance = null;
        instance.setDisplayname(displayname);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getScorename method, of class SScore.
     */
    @Test
    public void testGetScorename() {
        System.out.println("getScorename");
        SScore instance = null;
        String expResult = "";
        String result = instance.getScorename();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setScorename method, of class SScore.
     */
    @Test
    public void testSetScorename() {
        System.out.println("setScorename");
        String scorename = "";
        SScore instance = null;
        instance.setScorename(scorename);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getScore method, of class SScore.
     */
    @Test
    public void testGetScore() {
        System.out.println("getScore");
        SScore instance = null;
        int expResult = 0;
        int result = instance.getScore();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setScore method, of class SScore.
     */
    @Test
    public void testSetScore() {
        System.out.println("setScore");
        int score = 0;
        SScore instance = null;
        instance.setScore(score);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setEnabled method, of class SScore.
     */
    @Test
    public void testSetEnabled() {
        System.out.println("setEnabled");
        SScore instance = null;
        instance.setEnabled();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}