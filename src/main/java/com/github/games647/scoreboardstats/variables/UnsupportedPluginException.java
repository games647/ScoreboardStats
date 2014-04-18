package com.github.games647.scoreboardstats.variables;

/**
 * Represents an exception if the plugin isn't supported by a replacer.
 */
public class UnsupportedPluginException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new unsupported plugin exception
     */
    public UnsupportedPluginException() {
        super();
    }

    /**
     * Creates a new exception with a specific message
     *
     * @param message additional information
     */
    public UnsupportedPluginException(String message) {
        super(message);
    }
}
