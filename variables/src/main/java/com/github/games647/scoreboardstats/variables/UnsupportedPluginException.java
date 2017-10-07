package com.github.games647.scoreboardstats.variables;

public class UnsupportedPluginException extends ReplacerException {

    public UnsupportedPluginException(String pluginName, String expectedVersion, String currentVersion) {
        super(String.format("The version %s of plugin %s version isn't supported. We require at least %s",
                currentVersion, pluginName, expectedVersion));
    }

    public UnsupportedPluginException(String message) {
        super(message);
    }
}
