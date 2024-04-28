package io.github.itzispyder.improperui.config.settings.types;

import io.github.itzispyder.improperui.config.Dictionary;
import io.github.itzispyder.improperui.config.Setting;
import io.github.itzispyder.improperui.config.settings.SettingBuilder;
import io.github.itzispyder.improperui.render.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class DictionarySetting<T> extends Setting<Dictionary<T>> {

    protected DictionarySetting(String name, List<T> values, Function<T, String> definition, Function<String, T> lookup) {
        super(name, new Dictionary<>(values, definition, lookup));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Element toGuiElement(int x, int y) {
        return null;
    }

    public static <T> Builder<T> create(Class<T> type) {
        return new Builder<>();
    }

    public static class Builder<T> extends SettingBuilder<Dictionary<T>, Builder<T>, DictionarySetting<T>> {
        private List<T> values;
        private Function<T, String> definition;
        private Function<String, T> lookup;

        public Builder() {
            values = new ArrayList<>();
            definition = Objects::toString;
            lookup = str -> {
                for (T value : values)
                    if (definition.apply(value).equals(str))
                        return value;
                return null;
            };
        }

        @SuppressWarnings("unchecked")
        public Builder<T> defValues(List<?> values) {
            this.values = (List<T>)values;
            return this;
        }

        @SuppressWarnings("unchecked")
        public Builder<T> defValues(Iterable<?> values) {
            List<T> l = new ArrayList<>();
            for (var var : (Iterable<T>)values)
                l.add(var);
            this.values = l;
            return this;
        }

        @SuppressWarnings("unchecked")
        public Builder<T> defValues(Iterator<?> values) {
            List<T> l = new ArrayList<>();
            while (values.hasNext())
                l.add(((Iterator<T>) values).next());
            this.values = l;
            return this;
        }

        public Builder<T> defDefinition(Function<T, String> definition) {
            this.definition = definition;
            return this;
        }

        public Builder<T> defLookup(Function<String, T> lookup) {
            this.lookup = lookup;
            return this;
        }

        @Override
        protected DictionarySetting<T> buildSetting() {
            return new DictionarySetting<>(name, values, definition, lookup);
        }
    }
}
