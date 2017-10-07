package com.github.games647.scoreboardstats.variables;

import com.google.common.base.Converter;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

public class Replacer {

    private final Plugin plugin;
    private final String variable;

    private final Converter<String, Integer> stringConverter = Ints.stringConverter();
    private final Converter<Integer, String> intConverter = stringConverter.reverse();

    private final Map<String, EventReplacer<?>> eventsReplacers = Maps.newHashMap();

    private boolean async;
    private boolean constant;
    private String description;
    private boolean global;

    private Function<Player, Integer> scoreSupplier;
    private Function<Player, String> supplier;

    public Replacer(Plugin plugin, String variable) {
        this.plugin = plugin;
        this.variable = variable;
    }

    public Replacer async() {
        this.async = true;
        return this;
    }

    public Replacer constant() {
        this.constant = true;
        return this;
    }

    public Replacer description(String description) {
        this.description = ChatColor.translateAlternateColorCodes('&', description);
        return this;
    }

    public Replacer supply(Supplier<String> supplier) {
        this.global = true;
        supply(player -> supplier.get());
        return this;
    }

    public Replacer scoreSupply(IntSupplier supplier) {
        this.global = true;
        scoreSupply(player -> supplier.getAsInt());
        return this;
    }

    public Replacer supply(Function<Player, String> supplier) {
        this.supplier = supplier;
        this.scoreSupplier = player -> stringConverter.convert(supplier.apply(player));

        return this;
    }

    public Replacer scoreSupply(Function<Player, Integer> supplier) {
        this.scoreSupplier = supplier;
        this.supplier = player -> intConverter.convert(supplier.apply(player));

        return this;
    }

    public <T extends Event> Replacer event(Class<T> eventClass, Supplier<String> supplier) {
        event(eventClass, event -> supplier.get());
        return this;
    }

    public <T extends Event> Replacer eventScore(Class<T> eventClass, IntSupplier supplier) {
        eventScore(eventClass, event -> supplier.getAsInt());
        return this;
    }

    public <T extends Event> Replacer event(Class<T> eventClass, Function<T, String> function) {
        String eventName = eventClass.getCanonicalName();
        EventReplacer<?> replacer = eventsReplacers.get(eventName);
        if (replacer == null) {
            EventReplacer<T> checkedReplacer = new EventReplacer<>(this, eventClass);
            checkedReplacer.addFct(function);
        } else {
            ((EventReplacer<T>) replacer).addFct(function);
        }

        return this;
    }

    public <T extends Event> Replacer eventScore(Class<T> eventClass, Function<T, Integer> function) {
        String eventName = eventClass.getCanonicalName();
        EventReplacer<?> replacer = eventsReplacers.get(eventName);
        if (replacer == null) {
            EventReplacer<T> checkedReplacer = new EventReplacer<>(this, eventClass);
            checkedReplacer.addScoreFct(function);
        } else {
            ((EventReplacer<T>) replacer).addScoreFct(function);
        }

        return this;
    }

    public String replace(Player player) {
        return supplier.apply(player);
    }

    public int scoreReplace(Player player) {
        return scoreSupplier.apply(player);
    }

    public String getVariable() {
        return variable;
    }

    public boolean isAsync() {
        return async;
    }

    public boolean isGlobal() {
        return global;
    }

    public boolean isConstant() {
        return constant;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public boolean isEventVariable() {
        return !eventsReplacers.isEmpty();
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    protected Function<Player, Integer> getScoreSupplier() {
        return scoreSupplier;
    }

    protected Function<Player, String> getSupplier() {
        return supplier;
    }

    protected Map<String, EventReplacer<? extends Event>> getEventsReplacers() {
        return eventsReplacers;
    }
}
