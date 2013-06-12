package com.github.games647.scoreboardstats.packet;


public final class SScore {

    private String  displayname;
    private String  scorename;

    private int     score;
    private boolean disabled;

    public SScore(final String displayname, final String scorename, final int score) {
        this.displayname    = displayname;
        this.scorename      = scorename;
        this.score          = score;
    }

    public SScore(final String displayname, final int score) {
        this.displayname    = displayname;
        this.scorename      = displayname;
        this.score          = score;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(final String displayname) {
        this.displayname = displayname;
    }

    public String getScorename() {
        return scorename;
    }

    public void setScorename(final String scorename) {
        this.scorename = scorename;
    }

    public int getScore() {
        return score;
    }

    public void setScore(final int score) {
        this.score = score;
    }
}
