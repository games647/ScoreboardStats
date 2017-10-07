package com.github.games647.scoreboardstats.defaults;

import com.github.games647.scoreboardstats.variables.DefaultReplacer;
import com.github.games647.scoreboardstats.variables.DefaultReplacers;
import com.github.games647.scoreboardstats.variables.ReplacerAPI;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

@DefaultReplacer
public class BungeeCordVariables extends DefaultReplacers<Plugin> implements PluginMessageListener, Runnable {

    private static final int UPDATE_INTERVAL = 20 * 30;
    private static final String BUNGEE_CHANNEL = "BungeeCord";

    private int onlinePlayers;

    public BungeeCordVariables(ReplacerAPI replaceManager, Plugin plugin) {
        super(replaceManager, plugin);
    }

    @Override
    public void register() {
        Messenger messenger = Bukkit.getMessenger();
        messenger.registerOutgoingPluginChannel(plugin, BUNGEE_CHANNEL);
        messenger.registerIncomingPluginChannel(plugin, BUNGEE_CHANNEL, this);

        Bukkit.getScheduler().runTaskTimer(plugin, this, UPDATE_INTERVAL, UPDATE_INTERVAL);

        register("bungee-online").scoreSupply(() -> onlinePlayers);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(BUNGEE_CHANNEL)) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        if ("PlayerCount".equals(subChannel)) {
            try {
                String server = in.readUTF();
                int count = in.readInt();

                if ("ALL".equals(server)) {
                    onlinePlayers = count;
                }
            } catch (Exception eofException) {
                //happens if bungeecord doesn't know the server
                //ignore the admin should be notified by seeing the -1
            }
        }
    }

    @Override
    public void run() {
        Player sender = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if (sender != null) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("PlayerCount");
            out.writeUTF("ALL");

            sender.sendPluginMessage(plugin, BUNGEE_CHANNEL, out.toByteArray());
        }
    }
}
