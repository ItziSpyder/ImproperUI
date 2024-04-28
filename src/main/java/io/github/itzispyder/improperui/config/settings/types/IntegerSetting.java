package io.github.itzispyder.improperui.config.settings.types;

import io.github.itzispyder.improperui.config.settings.SettingBuilder;
import io.github.itzispyder.improperui.render.Element;
import io.github.itzispyder.improperui.util.MathUtils;

public class IntegerSetting extends NumberSetting<Integer> {

    public IntegerSetting(String name, int def, int val, int min, int max) {
        super(name, def, val, min, max);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Element toGuiElement(int x, int y) {
        return null;
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends SettingBuilder<Integer, Builder, IntegerSetting> {

        private int min, max;

        public Builder() {
            this.min = 0;
            this.max = 1;
        }

        public Builder min(int min) {
            this.min = Math.min(min, max);
            return this;
        }

        public Builder max(int max) {
            this.max = Math.max(min, max);
            return this;
        }

        @Override
        public IntegerSetting buildSetting() {
            return new IntegerSetting(name, MathUtils.clamp(def, min, max), getOrDef(val, def), min, max);
        }
    }
}
