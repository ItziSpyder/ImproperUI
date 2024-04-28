package io.github.itzispyder.improperui.config;

import java.util.*;
import java.util.function.Function;

public class Dictionary<T> {

    private final Map<String, Boolean> dictionary;
    private final Function<T, String> definition;
    private final Function<String, T> lookup;

    public Dictionary(List<T> values, Function<T, String> definition, Function<String, T> lookup) {
        this.dictionary = new HashMap<>();
        this.definition = definition;
        this.lookup = lookup;
        values.forEach(value -> dictionary.put(definition.apply(value), false));
    }

    @SuppressWarnings("unchecked")
    public boolean lookup(Object key) {
        return dictionary.getOrDefault(definition.apply((T)key), false);
    }

    @SuppressWarnings("unchecked")
    public void define(Object key, boolean value) {
        dictionary.put(definition.apply((T)key), value);
    }

    public List<T> getTrues() {
        return dictionary.keySet().stream()
                .filter(key -> dictionary.getOrDefault(key, false))
                .map(lookup)
                .filter(Objects::nonNull)
                .toList();
    }

    public List<T> getFalses() {
        return dictionary.keySet().stream()
                .filter(key -> !dictionary.getOrDefault(key, false))
                .map(lookup)
                .filter(Objects::nonNull)
                .toList();
    }

    public void setAll(boolean value) {
        Set<String> set = new HashSet<>(dictionary.keySet());
        set.forEach(key -> dictionary.put(key, value));
    }

    public void toggleAll() {
        Set<String> set = new HashSet<>(dictionary.keySet());
        set.forEach(key -> dictionary.put(key, !dictionary.getOrDefault(key, false)));
    }

    public Function<T, String> getDefinition() {
        return definition;
    }

    public Function<String, T> getLookup() {
        return lookup;
    }

    public Map<String, Boolean> getDictionary() {
        return new HashMap<>(dictionary);
    }

    public void overwrite(Map<String, Boolean> dictionary, boolean keepOld) {
        if (!keepOld)
            clear();
        this.dictionary.putAll(dictionary);
    }

    public void clear() {
        dictionary.clear();
    }

    public int size() {
        return dictionary.size();
    }
}
