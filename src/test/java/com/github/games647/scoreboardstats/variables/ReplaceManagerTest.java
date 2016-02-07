package com.github.games647.scoreboardstats.variables;

import com.github.games647.scoreboardstats.ScoreboardStats;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PrepareForTest({Bukkit.class, SimplePluginManager.class, Plugin.class, ScoreboardStats.class})
@RunWith(PowerMockRunner.class)
public class ReplaceManagerTest {

    private static final String SAMPLE_VARIABLE = "sample";

    @Test
    public void testUnregister() throws Exception {
        PowerMockito.mockStatic(Bukkit.class);
        Mockito.when(Bukkit.getPluginManager()).thenReturn(PowerMockito.mock(SimplePluginManager.class));

        ScoreboardStats plugin = PowerMockito.mock(ScoreboardStats.class);
        PowerMockito.when(plugin.getLogger()).thenReturn(Logger.getGlobal());

        ReplaceManager replaceManager = new ReplaceManager(null, plugin);

        testLegacy(replaceManager);
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

    @SuppressWarnings("deprecation")
    private void testLegacy(ReplaceManager replaceManager) {
        Replaceable legacyReplaceable = new Replaceable() {

            @Override
            public int getScoreValue(Player player, String variable) {
                throw new UnsupportedOperationException("Not supported yet");
            }
        };
        LegacyReplaceWrapper legacyWrapper = new LegacyReplaceWrapper(Bukkit
                .getPluginManager().getPlugin("pluginName"), legacyReplaceable);
        legacyWrapper.getVariables().add(SAMPLE_VARIABLE);
        replaceManager.register(legacyWrapper);
        Assert.assertTrue(replaceManager.unregister(legacyReplaceable));
    }
}
