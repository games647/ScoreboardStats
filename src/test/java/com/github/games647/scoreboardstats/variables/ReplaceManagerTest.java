package com.github.games647.scoreboardstats.variables;

import com.github.games647.scoreboardstats.ScoreboardStats;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
import org.slf4j.LoggerFactory;

@PrepareForTest({Bukkit.class, SimplePluginManager.class, Plugin.class, ScoreboardStats.class})
@RunWith(PowerMockRunner.class)
public class ReplaceManagerTest {

    private static final String SAMPLE_VARIABLE = "sample";

    @Test
    public void testUnregister() throws Exception {
        PowerMockito.mockStatic(Bukkit.class);
        Mockito.when(Bukkit.getPluginManager()).thenReturn(PowerMockito.mock(SimplePluginManager.class));
        Mockito.when(Bukkit.getMessenger()).thenReturn(PowerMockito.mock(StandardMessenger.class));
        Mockito.when(Bukkit.getScheduler()).thenReturn(PowerMockito.mock(BukkitScheduler.class));

        ScoreboardStats plugin = PowerMockito.mock(ScoreboardStats.class);
        PowerMockito.when(plugin.getLog()).thenReturn(LoggerFactory.getLogger("ScoreboardStats"));

        ReplaceManager replaceManager = new ReplaceManager(null, plugin);
        testAbstract(plugin, replaceManager);
    }

    private void testAbstract(Plugin plugin, ReplaceManager replaceManager) {
        VariableReplaceAdapter<?> replaceAdapter = new VariableReplaceAdapter<Plugin>(plugin, SAMPLE_VARIABLE) {

            @Override
            public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
                throw new UnsupportedOperationException("Not supported yet");
            }
        };

        replaceManager.register(replaceAdapter);
        Assert.assertTrue(replaceManager.unregister(replaceAdapter));
    }
}
