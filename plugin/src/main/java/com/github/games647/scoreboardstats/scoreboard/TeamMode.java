package com.github.games647.scoreboardstats.scoreboard;

import java.util.Optional;

public enum TeamMode {

    CREATE,

    REMOVE,

    UPDATE,

    ADD_MEMBER,

    REMOVE_MEMBER;

    public static Optional<TeamMode> getMode(int id) {
        TeamMode[] values = TeamMode.values();
        if (id < 0 || id >= values.length) {
            return Optional.empty();
        }

        return Optional.of(values[id]);
    }
}
