package com.github.games647.scoreboardstats.variables;

import com.github.games647.scoreboardstats.ScoreboardStats;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Tests if old replacers can still be registered and executed
 */
@PrepareForTest({Bukkit.class, SimplePluginManager.class, Plugin.class, ScoreboardStats.class})
@RunWith(PowerMockRunner.class)
@SuppressWarnings("deprecation")
public class LegacyReplacerTest {

    @Test
    public void getVariable() throws UnknownVariableException {
        PowerMockito.mockStatic(Bukkit.class);
        Mockito.when(Bukkit.getPluginManager()).thenReturn(PowerMockito.mock(SimplePluginManager.class));
        Mockito.when(Bukkit.getMessenger()).thenReturn(PowerMockito.mock(StandardMessenger.class));
        Mockito.when(Bukkit.getScheduler()).thenReturn(PowerMockito.mock(BukkitScheduler.class));

        ScoreboardStats plugin = PowerMockito.mock(ScoreboardStats.class);
        PowerMockito.when(plugin.getLogger()).thenReturn(Logger.getGlobal());
        Mockito.when(plugin.getLogger()).thenReturn(Logger.getAnonymousLogger());

        ReplaceManager replaceManager = new ReplaceManager(null, plugin);
        replaceManager.register((player, variable) -> {
            Logger.getAnonymousLogger().log(Level.INFO, "Replaced variable: {0}", variable);
            Assert.assertTrue(variable.charAt(0) == '%');
            Assert.assertTrue(variable.endsWith("%"));
            return 0;
        }, "pluginName");

        replaceManager.getScore(null, "variableName", "test", -1, true);
    }
}
