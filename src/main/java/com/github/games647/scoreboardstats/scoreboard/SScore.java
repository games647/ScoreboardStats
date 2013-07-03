package com.github.games647.scoreboardstats.scoreboard;

import net.minecraft.server.v1_6_R1.Packet207SetScoreboardScore;

import org.apache.commons.lang.Validate;


public final class SScore {

    private String  scorename;
    private String  displayname;

    private int     score       = Integer.MIN_VALUE;
    private boolean disabled;

    public SScore(String displayname, String scorename, int score) {
        Validate.isTrue(displayname.length() > 16, "The display name for the score can't be longer than 16 characters");
        Validate.isTrue(scorename.length() > 16, "The score name can't be longer than 16 characters");

        this.displayname    = displayname;
        this.scorename      = scorename;
        this.score          = score;
    }

    public SScore(String displayname, int score) {
        Validate.isTrue(displayname.length() > 16, "The display name for the score can't be longer than 16 characters");

        this.displayname    = displayname;
        this.scorename      = displayname;
        this.score          = score;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        Validate.isTrue(displayname.length() > 16, "The display name for the score can't be longer than 16 characters");
        this.displayname = displayname;
    }

    public String getScorename() {
        return scorename;
    }

    public void setScorename(String scorename) {
        Validate.isTrue(scorename.length() > 16, "The score name can't be longer than 16 characters");
        this.scorename = scorename;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setEnabled() {
        final Packet207SetScoreboardScore packet = new Packet207SetScoreboardScore();

        packet.a = scorename;
        packet.b = displayname;
        packet.d = score;
    }
//
//    public void setDisabled() {
//
//    }
//
//    public void update() {
//
//    }
}
