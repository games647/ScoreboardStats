package com.github.games647.scoreboardstats.packet;


public final class SScore {

    private String displayname;
    private String objectivename;
    private int score;

    public SScore(final String displayname, final String objectivename, final int score) {
        this.displayname = displayname;
        this.objectivename = objectivename;
        this.score = score;
    }

    public SScore(final String displayname, final int score) {
        this.displayname = displayname;
        this.objectivename = displayname;
        this.score = score;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(final String displayname) {
        this.displayname = displayname;
    }

    public String getObjectivename() {
        return objectivename;
    }

    public void setObjectivename(final String objectivename) {
        this.objectivename = objectivename;
    }

    public int getScore() {
        return score;
    }

    public void setScore(final int score) {
        this.score = score;
    }
}
