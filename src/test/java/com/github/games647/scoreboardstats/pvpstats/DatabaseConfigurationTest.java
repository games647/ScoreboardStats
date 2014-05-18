package com.github.games647.scoreboardstats.pvpstats;

import java.io.UnsupportedEncodingException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.Assert;
import org.junit.Test;

public class DatabaseConfigurationTest {

    /**
     * Test of validatePath method, of class DatabaseConfiguration.
     * 
     * @throws java.lang.Exception
     */
    @Test
    public void testValidatePath() throws Exception {
        DatabaseConfiguration.validatePath("/D:/fs46/abc.jar");

        //umlauts
        DatabaseConfiguration.validatePath("/D:/äüöodad4/abc.jar");

        //linux
        DatabaseConfiguration.validatePath("/home/xyz/abc.jar");

    }

    @Test
    public void testValidatePathFailure() throws UnsupportedEncodingException {
        expectedExceptionValidation("C:\\Users\\InvitÃ©\\Desktop\\Server\\craftbukkit.jar");

        expectedExceptionValidation("C:\\Users\\РђРґРјРё\\Desktop\\server\\craftbukkit.jar");
    }

    private void expectedExceptionValidation(String path) {
        try {
            DatabaseConfiguration.validatePath(path);
        } catch (InvalidConfigurationException ex) {
            return;
        }

        Assert.fail("Expected exception doesn't occured: " + path);
    }
}
