package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.BackwardsCompatibleUtil;
import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeCordVariables extends DefaultReplaceAdapter<Plugin> implements PluginMessageListener, Runnable {

    private static final int UPDATE_INTERVAL = 20 * 30;
    private static final String BUNGEE_CHANNEL = "BungeeCord";

    private final ReplaceManager replaceManager;
    private Map<String, Integer> playersCount = Maps.newHashMap();

    public BungeeCordVariables(ReplaceManager replaceManager) {
        super(Bukkit.getPluginManager().getPlugin("ScoreboardStats"), "", true, false, true
                , "bungee-online", "bungee_*");

        this.replaceManager = replaceManager;
        Bukkit.getMessenger().registerOutgoingPluginChannel(getPlugin(), BUNGEE_CHANNEL);
        Bukkit.getMessenger().registerIncomingPluginChannel(getPlugin(), BUNGEE_CHANNEL, this);

        Bukkit.getScheduler().runTaskTimer(getPlugin(), this, UPDATE_INTERVAL, UPDATE_INTERVAL);
        playersCount.put("ALL", -1);
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        if ("bungee-online".equals(variable)) {
            replaceEvent.setScoreOrText(playersCount.get("ALL"));
        } else {
            String serverName = variable.replace("bungee_", "");
            int count = playersCount.computeIfAbsent(serverName, key -> -1);
            replaceEvent.setScoreOrText(count);
        }
        
        replaceEvent.setConstant(true);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(BUNGEE_CHANNEL)) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if ("PlayerCount".equals(subchannel)) {
            try {
                String server = in.readUTF();
                int count = in.readInt();
                //update variable for cache
                playersCount.put(server, count);

                if ("ALL".equals(server)) {
                    replaceManager.updateScore("bungee-online", count);
                } else {
                    replaceManager.updateScore("bungee_" + server, count);
                }
            } catch (Exception eofException) {
                //happens if bungeecord doesn't know the server
                //ignore the admin should be notified by seeing the -1
            }
        }
    }

    @Override
    public void run() {
        Player sender = Iterables.getFirst(BackwardsCompatibleUtil.getOnlinePlayers(), null);
        if (sender != null) {
            for (String serverName : playersCount.keySet()) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("PlayerCount");
                out.writeUTF(serverName);

                sender.sendPluginMessage(getPlugin(), BUNGEE_CHANNEL, out.toByteArray());
            }
        }
    }
}
