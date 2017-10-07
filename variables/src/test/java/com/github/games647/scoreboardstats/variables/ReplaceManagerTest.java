package com.github.games647.scoreboardstats.variables;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.LoggerFactory;

@PrepareForTest({Bukkit.class, SimplePluginManager.class, Plugin.class})
@RunWith(PowerMockRunner.class)
public class ReplaceManagerTest {

    private static final String SAMPLE_VARIABLE = "sample";

    @Test
    public void testUnregister() throws Exception {
        PowerMockito.mockStatic(Bukkit.class);
        Mockito.when(Bukkit.getPluginManager()).thenReturn(PowerMockito.mock(SimplePluginManager.class));
        Mockito.when(Bukkit.getMessenger()).thenReturn(PowerMockito.mock(StandardMessenger.class));
        Mockito.when(Bukkit.getScheduler()).thenReturn(PowerMockito.mock(BukkitScheduler.class));

        Plugin plugin = PowerMockito.mock(Plugin.class);

        ReplaceManager replaceManager = new ReplaceManager(null, plugin, LoggerFactory.getLogger("test"));
        replaceManager.register(new Replacer(plugin, "test").scoreSupply(() -> 1));
    }
}
