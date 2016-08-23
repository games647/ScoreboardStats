package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.BackwardsCompatibleUtil;
import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeCordVariables extends DefaultReplaceAdapter<Plugin> implements PluginMessageListener, Runnable {

    private static final int UPDATE_INTERVAL = 20 * 30;
    private static final String BUNGEE_CHANNEL = "BungeeCord";

    private final ReplaceManager replaceManager;
    private int playersCount;

    public BungeeCordVariables(ReplaceManager replaceManager) {
        super(Bukkit.getPluginManager().getPlugin("ScoreboardStats"), "bungee-online");

        this.replaceManager = replaceManager;
        Bukkit.getMessenger().registerOutgoingPluginChannel(getPlugin(), BUNGEE_CHANNEL);
        Bukkit.getMessenger().registerIncomingPluginChannel(getPlugin(), BUNGEE_CHANNEL, this);

        Bukkit.getScheduler().runTaskTimer(getPlugin(), this, UPDATE_INTERVAL, UPDATE_INTERVAL);
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        replaceEvent.setScoreOrText(playersCount);
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
            String server = in.readUTF();
            //update variable for cache
            playersCount = in.readInt();
            replaceManager.updateScore("bungee-online", playersCount);
        }
    }

    @Override
    public void run() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerCount");
        out.writeUTF("ALL");

        Player sender = Iterables.getFirst(BackwardsCompatibleUtil.getOnlinePlayers(), null);
        if (sender != null) {
            sender.sendPluginMessage(getPlugin(), BUNGEE_CHANNEL, out.toByteArray());
        }
    }
}
