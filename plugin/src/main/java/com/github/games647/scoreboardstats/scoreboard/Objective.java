package com.github.games647.scoreboardstats.scoreboard;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.ScoreboardAction;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;

import org.apache.commons.lang.builder.ToStringBuilder;

import static com.comphenix.protocol.PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE;
import static com.comphenix.protocol.PacketType.Play.Server.SCOREBOARD_OBJECTIVE;
import static com.comphenix.protocol.PacketType.Play.Server.SCOREBOARD_SCORE;

/**
 * Represents a sidebar objective
 */
public class Objective {

    private static final int MAX_ITEM_SIZE = 15;
    private static final int SIDEBAR_SLOT = 1;

    //A scoreboard can only hold < 16 scores
    final Map<String, Integer> scores = Maps.newHashMapWithExpectedSize(MAX_ITEM_SIZE);

    private final PlayerScoreboard scoreboard;
    private final String objectiveId;
    String displayName;

    Objective(PlayerScoreboard scoreboard, String objectiveId, String displayName) {
        this.scoreboard = scoreboard;

        this.objectiveId = objectiveId;
        this.displayName = displayName;
    }

    public boolean isShown() {
        //Prevents NPE with this ordering
        return scoreboard.getSidebarObjective().map(sidebar -> sidebar.equals(this)).orElse(false);
    }

    public boolean exists() {
        return scoreboard.getObjective(objectiveId).isPresent();
    }

    public String getId() {
        return objectiveId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        if (this.displayName.equals(displayName)) {
            return;
        }

        this.displayName = displayName;
        sendObjectivePacket(State.UPDATE_DISPLAY_NAME);
    }

    public OptionalInt getScore(String name) {
        Integer score = scores.get(name);
        if (score == null) {
            return OptionalInt.empty();
        }

        return OptionalInt.of(score);
    }

    public int getScore(String name, int def) {
        Integer score = scores.get(name);
        if (score == null) {
            setScores(name, def);
            return def;
        }

        return score;
    }

    public boolean hasScore(String name) {
        return scores.containsKey(name);
    }

    public void setScores(String name, int value) {
        Integer oldVal = scores.put(name, value);
        if (oldVal != null && oldVal == value) {
            return;
        }

        sendScorePacket(name, value, ScoreboardAction.CHANGE);
    }

    public List<Map.Entry<String, Integer>> getScores() {
        List<Map.Entry<String, Integer>> values = Lists.newArrayListWithExpectedSize(scores.size());
        values.addAll(scores.entrySet());
        values.sort((score1, score2) -> Integer.compare(score2.getValue(), score1.getValue()));
        return values;
    }

    public void removeScore(String name) {
        scores.remove(name);
        sendScorePacket(name, 0, ScoreboardAction.REMOVE);
    }

    public void clear() {
        scores.keySet().forEach(this::removeScore);
    }

    public PlayerScoreboard getScoreboard() {
        return scoreboard;
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectiveId);
    }

    @Override
    public boolean equals(Object obj) {
        //ignores also null
        return obj instanceof Objective && Objects.equals(objectiveId, ((Objective) obj).objectiveId);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    private void sendScorePacket(String name, int score, ScoreboardAction action) {
        PacketContainer packet = new PacketContainer(SCOREBOARD_SCORE);
        packet.getStrings().write(0, name);
        packet.getStrings().write(1, objectiveId);

        packet.getIntegers().write(0, score);

        packet.getScoreboardActions().write(0, action);

        scoreboard.sendPacket(packet);
    }

    void sendObjectivePacket(State state) {
        PacketContainer packet = new PacketContainer(SCOREBOARD_OBJECTIVE);
        packet.getStrings().write(0, objectiveId);

        if (state != State.REMOVE) {
            packet.getStrings().write(1, displayName);
            packet.getStrings().write(2, "integer");
        }

        scoreboard.sendPacket(packet);
    }

    void sendShowPacket() {
        PacketContainer packet = new PacketContainer(SCOREBOARD_DISPLAY_OBJECTIVE);
        packet.getStrings().write(0, objectiveId);

        packet.getIntegers().write(0, SIDEBAR_SLOT);
        scoreboard.sendPacket(packet);
    }
}
