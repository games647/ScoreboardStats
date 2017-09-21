package com.github.games647.scoreboardstats.scoreboard.protocol;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.ScoreboardAction;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static com.comphenix.protocol.PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE;
import static com.comphenix.protocol.PacketType.Play.Server.SCOREBOARD_OBJECTIVE;
import static com.comphenix.protocol.PacketType.Play.Server.SCOREBOARD_SCORE;
import static com.comphenix.protocol.PacketType.Play.Server.SCOREBOARD_TEAM;

/**
 * Creates the specific packets and send it with help of ProtocolLib.
 *
 * Protocol specifications can be found here http://wiki.vg/Protocol
 *
 * @see PacketListener
 * @see ProtocolManager
 */
public class PacketFactory {

    private static final int SIDEBAR_SLOT = 1;

    private static final ProtocolManager PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager();

    /**
     * Sends a new scoreboard item packet.
     *
     * @param item the scoreboard item
     * @param state whether the item should be send as removed or created/updated
     */
    public static void sendPacket(Item item, State state) {
        PacketContainer scorePacket = PROTOCOL_MANAGER.createPacket(SCOREBOARD_SCORE, true);
        //max length 16 and since 1.7 UTF-8 instead of UTF-16
        scorePacket.getStrings().write(0, item.getScoreName());
        scorePacket.getStrings().write(1, item.getParent().getName());

        if (State.REMOVE != state) {
            //Only need these if the score will be updated or created
            scorePacket.getIntegers().write(0, item.getScore());
        }

        //state id
        scorePacket.getScoreboardActions().write(0, ScoreboardAction.values()[state.ordinal()]);
        sendPacket(item.getParent().getScoreboard().getOwner().getUniqueId(), scorePacket);
    }

    /**
     * Sends a new scoreboard objective packet and a display packet (if the objective was created)
     *
     * @param objective the scoreboard objective
     * @param state whether the objective was created, updated (display-name) or removed
     */
    public static void sendPacket(Objective objective, State state) {
        PacketContainer objectivePacket = PROTOCOL_MANAGER.createPacket(SCOREBOARD_OBJECTIVE, true);
        objectivePacket.getStrings().write(0, objective.getName());

        if (state != State.REMOVE) {
            //only send the title if needed, so while creating the objective or update the title
            //max length 32 and since 1.7 UTF-8 instead of UTF-16
            objectivePacket.getStrings().write(1, objective.getDisplayName());
            //introduced in 1.8
            objectivePacket.getStrings().write(2, "integer");
        }

        //state id
        objectivePacket.getIntegers().write(0, state.ordinal());
        sendPacket(objective.getScoreboard().getOwner().getUniqueId(), objectivePacket);
    }

    /**
     * Set the sidebar slot to this objective
     *
     * @param objective the displayed objective, if getName() is empty it will just clear the sidebar
     */
    public static void sendDisplayPacket(Objective objective) {
        PacketContainer displayPacket = PROTOCOL_MANAGER.createPacket(SCOREBOARD_DISPLAY_OBJECTIVE, true);
        //Can be empty to clear the sidebar slot
        //max length 16 and since 1.7 UTF-8 instead of UTF-16
        displayPacket.getStrings().write(0, objective.getName());

        displayPacket.getIntegers().write(0, SIDEBAR_SLOT);
        sendPacket(objective.getScoreboard().getOwner().getUniqueId(), displayPacket);
    }

    public static void sendTeamPacket(Team team, State state) {
        PacketContainer teamPacket = PROTOCOL_MANAGER.createPacket(SCOREBOARD_TEAM, true);
        teamPacket.getStrings().write(0, team.getName());

        if (state == State.CREATE) {
            teamPacket.getStrings().write(2, team.getPrefix());
            teamPacket.getStrings().write(3, team.getSuffix());

            teamPacket.getSpecificModifier(Collection.class).write(0, team.getEntries());
        } else if (state == State.UPDATE) {
            teamPacket.getStrings().write(2, team.getPrefix());
            teamPacket.getStrings().write(3, team.getSuffix());
        }

        teamPacket.getIntegers().write(1, state.ordinal());
//        sendPacket(team, teamPacket);
    }

    private static void sendPacket(UUID receiverId, PacketContainer packet) {
        Player receiver = Bukkit.getPlayer(receiverId);
        if (receiver == null) {
            return;
        }

        //add metadata that we ignore our packets on the listener
        packet.addMetadata("ScoreboardStats", true);

        try {
            //false so we don't listen to our own packets
            PROTOCOL_MANAGER.sendServerPacket(receiver, packet);
        } catch (InvocationTargetException ex) {
            //just log it for now.
            Logger.getLogger("ScoreboardStats").log(Level.SEVERE, null, ex);
        }
    }

    private PacketFactory() {
        //Utility class
    }
}
