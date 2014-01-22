package com.github.games647.scoreboardstats.variables;

/*
 * Represents an exception if a used variable can't be replaced by a replacer
 * So if no replacer know this variable
 */
public class UnknownVariableException extends Exception {

    private static final long serialVersionUID = 1L;

    public UnknownVariableException() {
        super();
    }

    public UnknownVariableException(String message) {
        super(message);
    }
}
