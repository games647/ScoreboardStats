package com.github.games647.scoreboardstats.variables;

public class VariableItem {

    private final boolean textVariable;
    private final String variable;

    private String displayText;
    private int score;

    public VariableItem(boolean textVariable, String variable, String displayText, int defaultScore) {
        this(textVariable, variable, displayText);

        this.score = defaultScore;
    }

    public VariableItem(boolean textVariable, String variable, String displayText) {
        this.textVariable = textVariable;
        this.variable = variable;
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isTextVariable() {
        return textVariable;
    }

    public String getVariable() {
        return variable;
    }

    @Override
    public String toString() {
        return "VariableItem{"
                + "textVariable=" + textVariable
                + ", variable=" + variable
                + ", displayText=" + displayText
                + ", score=" + score
                + '}';
    }
}
