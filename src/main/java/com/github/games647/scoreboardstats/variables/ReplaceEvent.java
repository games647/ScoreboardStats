package com.github.games647.scoreboardstats.variables;

import org.apache.commons.lang.StringUtils;

/**
 * Represents a variable replace action
 */
public class ReplaceEvent {

    private final String variable;
    private final boolean textVariable;

    private boolean constant;
    private boolean modified;
    private String displayText;
    private int score;

    /**
     * Creates a replace event
     *
     * @param variable the to replaced variable
     * @param textVariable whether it should return an String or Integer
     * @param displayText the scoreboard item name
     * @param score the scoreboard item score
     */
    public ReplaceEvent(String variable, boolean textVariable, String displayText, int score) {
        this.variable = variable;
        this.textVariable = textVariable;
        this.displayText = displayText;
        this.score = score;
    }

    /**
     * Get whether this event is modified
     *
     * @return whether this event is modified
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Get hether the variable is in the display name
     *
     * @return whether the variable is in the display name
     */
    public boolean isTextVariable() {
        return textVariable;
    }

    /**
     * Get whether it will be update with an event handler
     *
     * @return whether it will be update with an event handler
     */
    public boolean isConstant() {
        return constant;
    }

    /**
     * Set the constant status of the variable
     *
     * @param constant whether it will be update with an event handler
     */
    public void setConstant(boolean constant) {
        this.constant = constant;
    }

    /**
     * Get the display name
     *
     * @return the display name of this scoreboard item
     */
    public String getDisplayText() {
        return displayText;
    }

    /**
     * Set the entire display name of this scoreboard item to this text
     *
     * @param newDisplayText the new display name
     */
    public void setDisplayText(String newDisplayText) {
//        if (!displayText.equals(newDisplayText)) {
            touch();
            this.displayText = newDisplayText;
//        }
    }

    /**
     * Get the score of this scoreboard item
     *
     * @return the scoreboard item score
     */
    public int getScore() {
        return score;
    }

    /**
     * Set the score of this scoreboard item
     *
     * @param newScore new item score
     */
    public void setScore(int newScore) {
//        if (score != newScore) {
            touch();
            this.score = newScore;
//        }
    }

    /**
     * Set the score or display name of this scoreboard item
     *
     * @param newScore the new value
     */
    public void setScoreOrText(int newScore) {
        if (textVariable) {
            setDisplayText(StringUtils.replace(displayText, variable, Integer.toString(newScore), 1));
        } else {
            setScore(newScore);
        }
    }

    /**
     * Set the score or display name of this scoreboard item
     *
     * @param replacedVariable the new value
     */
    public void setScoreOrText(String replacedVariable) {
        if (textVariable) {
            //StringUtil.replace is fater because it doesn't create a pattern in the background
            //and we need the performance as the replace method is called often
            setDisplayText(StringUtils.replace(displayText, variable, replacedVariable, 1));
        } else {
            Integer parsedInt = Integer.getInteger(variable);
            if (parsedInt != null) {
                setScore(parsedInt);
            }
        }
    }

    /**
     * Trigger that the replacer have found the variable
     */
    public void touch() {
        modified = true;
    }
}
