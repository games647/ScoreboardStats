package com.github.games647.scoreboardstats.scoreboard;

import org.apache.commons.lang.Validate;


public final class SScore {

    private String  displayname;
    private String  scorename;

    private int     score   = Integer.MIN_VALUE;
    private boolean disabled;

    public SScore(final String displayname, final String scorename, final int score) {
        Validate.isTrue(displayname.length() > 16, "The display name for the score can't be longer than 16 characters");
        Validate.isTrue(scorename.length() > 16, "The score name can't be longer than 16 characters");

        this.displayname    = displayname;
        this.scorename      = scorename;
        this.score          = score;
    }

    public SScore(final String displayname, final int score) {
        Validate.isTrue(displayname.length() > 16, "The display name for the score can't be longer than 16 characters");

        this.displayname    = displayname;
        this.scorename      = displayname;
        this.score          = score;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(final String displayname) {
        Validate.isTrue(displayname.length() > 16, "The display name for the score can't be longer than 16 characters");
        this.displayname = displayname;
    }

    public String getScorename() {
        return scorename;
    }

    public void setScorename(final String scorename) {
        Validate.isTrue(scorename.length() > 16, "The score name can't be longer than 16 characters");
        this.scorename = scorename;
    }

    public int getScore() {
        return score;
    }

    public void setScore(final int score) {
        this.score = score;
    }
}
