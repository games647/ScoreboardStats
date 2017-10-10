package com.github.games647.scoreboardstats.variables;

import com.github.games647.scoreboardstats.BoardManager;
import com.github.games647.scoreboardstats.Version;
import com.google.common.collect.Sets;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;

/**
 * Handling the replace management
 */
public class ReplaceManager extends ReplacerAPI {

    private static final String UNSUPPORTED_VERSION = "The Replacer: {} cannot be registered -" +
            " the plugin version isn't supported {} {}";

    //todo: only temporarily
    @Deprecated
    private static ReplaceManager instance;
    private final Plugin plugin;
    private final BoardManager boardManager;

    /**
     * Creates a new replace manager
     *
     * @param scoreboardManager to manage the scoreboards
     * @param plugin            ScoreboardStats plugin
     */
    public ReplaceManager(BoardManager scoreboardManager, Plugin plugin, Logger logger) {
        super(logger);

        instance = this;

        this.plugin = plugin;
        this.boardManager = scoreboardManager;

        Bukkit.getPluginManager().registerEvents(new PluginListener(this), plugin);
        addDefaultReplacers();
    }

    @Deprecated
    public static ReplaceManager getInstance() {
        return instance;
    }

    public void close() {
        instance = null;
    }

    @Override
    public void forceUpdate(Player player, String variable, int score) {
        boardManager.updateVariable(player, variable, score);
    }

    @Override
    public void forceUpdate(Player player, String variable, String value) {
        boardManager.updateVariable(player, variable, value);
    }

    public void updateGlobals() {
        replacers.values()
                .stream()
                .filter(Replacer::isGlobal)
                .filter(replacer -> !replacer.isEventVariable())
                .forEach(replacer -> {
                    int score = replacer.scoreReplace(null);
                    String variable = replacer.getVariable();
                    Bukkit.getOnlinePlayers().forEach(player -> boardManager.updateVariable(player, variable, score));
                });
    }

    private void addDefaultReplacers() {
        Set<String> defaultReplacers = Sets.newHashSet();
        try {
            defaultReplacers = ClassPath.from(getClass().getClassLoader())
                    .getTopLevelClasses("com.github.games647.scoreboardstats.defaults")
                    .stream()
                    .map(ClassInfo::load)
                    .filter(DefaultReplacers.class::isAssignableFrom)
                    .map(clazz -> (Class<DefaultReplacers<?>>) clazz)
                    .filter(this::registerDefault)
                    .map(Class::getSimpleName)
                    .collect(Collectors.toSet());
        } catch (IOException ioEx) {
            logger.error("Failed to register replacers", ioEx);
        }

        logger.info("Registered default replacers: {}", defaultReplacers);
    }

    private boolean registerDefault(Class<DefaultReplacers<?>> replacerClass) {
        try {
            DefaultReplacer annotation = replacerClass.getAnnotation(DefaultReplacer.class);

            String replacerPluginName = annotation.plugin();
            if (replacerPluginName.isEmpty()) {
                replacerPluginName = plugin.getName();
            }

            Plugin replacerPlugin = Bukkit.getPluginManager().getPlugin(replacerPluginName);
            if (replacerPlugin != null) {
                String required = annotation.requiredVersion();
                String version = replacerPlugin.getDescription().getVersion();
                if (!required.isEmpty() && new Version(version).compareTo(new Version(required)) >= 0) {
                    logger.info(UNSUPPORTED_VERSION, replacerClass.getSimpleName(), version, required);
                    return false;
                }

                Constructor<DefaultReplacers<?>> cons = replacerClass.getConstructor(ReplacerAPI.class, Plugin.class);
                cons.newInstance(this, replacerPlugin).register();
            }

            return true;
        } catch (Exception | LinkageError replacerException) {
            //only catch this throwable, because they could probably happened
            logger.warn("Cannot register replacer", replacerException);
        }

        return false;
    }
}
