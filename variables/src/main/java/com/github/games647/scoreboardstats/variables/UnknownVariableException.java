package com.github.games647.scoreboardstats.variables;

/**
 * Represents an exception if a used variable can't be replaced by a replacer
 * So if no replacer know this variable
 */
public class UnknownVariableException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception if the variable couldn't be replaced.
     */
    public UnknownVariableException() {
        super();
    }

    /**
     * Creates a new exception if the variable couldn't be replaced.
     *
     * @param message additional information
     */
    public UnknownVariableException(String message) {
        super(message);
    }
}
