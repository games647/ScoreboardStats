package com.github.games647.scoreboardstats.scoreboard;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;

public class Team {

    private final PlayerScoreboard scoreboard;
    private final String teamId;

    final Set<String> members = Sets.newHashSet();
    String prefix;
    String suffix;

    public Team(PlayerScoreboard scoreboard, String teamId) {
        this.scoreboard = scoreboard;
        this.teamId = teamId;
    }

    public Team(PlayerScoreboard scoreboard, String teamId, Collection<String> members) {
        this(scoreboard, teamId);

        this.members.addAll(members);
    }

    public String getId() {
        return teamId;
    }

    public boolean hasMember(String member) {
        return members.contains(member);
    }

    public boolean addMember(String member) {
        return members.add(member);
    }

    public boolean removeMember(String member) {
        return members.remove(member);
    }

    public Set<String> getMembers() {
        return ImmutableSet.copyOf(members);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    void sendCreatePacket() {
        PacketContainer packet = newPacket(TeamMode.CREATE);
        packet.getSpecificModifier(Collection.class).write(0, ImmutableSet.copyOf(members));
        scoreboard.sendPacket(packet);
    }

    void sendMemberUpdatePacket(boolean add) {
        PacketContainer packet = newPacket(add ? TeamMode.ADD_MEMBER : TeamMode.REMOVE_MEMBER);
        packet.getSpecificModifier(Collection.class).write(0, ImmutableSet.copyOf(members));
        scoreboard.sendPacket(packet);
    }

    void sendRemovePacket() {
        PacketContainer packet = newPacket(TeamMode.REMOVE);
        scoreboard.sendPacket(packet);
    }

    private PacketContainer newPacket(TeamMode mode) {
        PacketContainer packet = new PacketContainer(Server.SCOREBOARD_TEAM);
        packet.getStrings().write(0, teamId);

        packet.getIntegers().write(1, mode.ordinal());
        return packet;
    }
}
