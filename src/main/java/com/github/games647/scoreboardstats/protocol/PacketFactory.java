package com.github.games647.scoreboardstats.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        final PacketContainer scorePacket = PROTOCOL_MANAGER
                .createPacket(PacketType.Play.Server.SCOREBOARD_SCORE, true);
        //max length 16 and since 1.7 UTF-8 instead of UTF-16
        scorePacket.getStrings().write(0, item.getScoreName());
        scorePacket.getStrings().write(1, item.getParent().getName());

        if (State.REMOVE != state) {
            //Only need these if the score will be updated or created
            scorePacket.getIntegers().write(0, item.getScore());
        }

        //state id
        final StructureModifier<Enum> enumModifier = scorePacket.getSpecificModifier(Enum.class);
        final Enum<?> scoreboardActions = enumModifier.readSafely(0);
        if (scoreboardActions == null) {
            //old system
            scorePacket.getIntegers().write(1, state.ordinal());
        } else {
            final Enum action = scoreboardActions.getClass().getEnumConstants()[state.ordinal()];
            enumModifier.write(0, action);
        }

        try {
            //false so we don't listen to our own packets
            PROTOCOL_MANAGER.sendServerPacket(item.getOwner(), scorePacket, false);
        } catch (InvocationTargetException ex) {
            //just log it for now.
            Logger.getLogger("ScoreboardStats").log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Sends a new scoreboard objective packet and a display packet (if the
     * objective was created)
     *
     * @param objective the scoreboard objective
     * @param state whether the objective was created, updated (displayname) or removed
     */
    public static void sendPacket(Objective objective, State state) {
        final PacketContainer objectivePacket = PROTOCOL_MANAGER
                .createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE, true);
//        max length 16 and since 1.7 UTF-8 instead of UTF-16
        objectivePacket.getStrings().write(0, objective.getName());

        if (state != State.REMOVE) {
            //only send the title if needed, so while creating the objective or update the title
            //max length 32 and since 1.7 UTF-8 instead of UTF-16
            objectivePacket.getStrings().write(1, objective.getDisplayName());
            //introduced in 1.8 don't fail on versions for 1.7 or below
            objectivePacket.getStrings().writeSafely(2, "integer");
        }

        //state id
        objectivePacket.getIntegers().write(0, state.ordinal());

        try {
            //false so we don't listen to our own packets
            PROTOCOL_MANAGER.sendServerPacket(objective.getOwner(), objectivePacket, false);
            sendDisplayPacket(objective);
        } catch (InvocationTargetException ex) {
            //just log it for now.
            Logger.getLogger("ScoreboardStats").log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Set the sidebar slot to this objective
     *
     * @param objective the displayed objective, if getName() is empty it will just clear the sidebar
     */
    public static void sendDisplayPacket(Objective objective) {
        final PacketContainer displayPacket = PROTOCOL_MANAGER
                .createPacket(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE, true);
        //Can be empty to clear the sidebar slot
        //max length 16 and since 1.7 UTF-8 instead of UTF-16
        displayPacket.getStrings().write(0, objective.getName());

        displayPacket.getIntegers().write(0, SIDEBAR_SLOT);

        try {
            //false so we don't listen to our own packets
            PROTOCOL_MANAGER.sendServerPacket(objective.getOwner(), displayPacket, false);
        } catch (InvocationTargetException ex) {
            //just log it for now.
            Logger.getLogger("ScoreboardStats").log(Level.SEVERE, null, ex);
        }
    }

    private PacketFactory() {
        //Utility class
    }
}
