package io.github.itzispyder.improperui.config.settings.types;

import io.github.itzispyder.improperui.config.Setting;
import io.github.itzispyder.improperui.config.settings.SettingBuilder;
import io.github.itzispyder.improperui.render.Element;

public class EnumSetting<T extends Enum<?>> extends Setting<T> {

    private final T[] array;

    @SuppressWarnings("unchecked")
    public EnumSetting(String name, T val) {
        super(name, val);
        this.array = (T[])val.getClass().getEnumConstants();
    }

    public T[] getArray() {
        return array;
    }

    public T valueOf(String name) {
        for (T t : array)
            if (t.name().equals(name))
                return t;
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Element toGuiElement(int x, int y) {
        return null;
    }

    public static <T extends Enum<?>> Builder<T> create(Class<T> type) {
        return new Builder<>();
    }

    public static class Builder<T extends Enum<?>> extends SettingBuilder<T, Builder<T>, EnumSetting<T>> {
        @Override
        protected EnumSetting<T> buildSetting() {
            return new EnumSetting<>(name, def);
        }
    }
}
