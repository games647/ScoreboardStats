package com.github.games647.scoreboardstats.variables;

/**
 * Represents an exception if a used variable can't be replaced by a replacer
 * So if no replacer know this variable
 */
public class UnknownVariableException extends ReplacerException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception if the variable couldn't be replaced.
     *
     * @param variable variable
     */
    public UnknownVariableException(String variable) {
        super("Unknown variable " + variable);
    }
}
