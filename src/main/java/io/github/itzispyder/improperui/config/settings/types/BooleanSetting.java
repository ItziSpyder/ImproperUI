package io.github.itzispyder.improperui.config.settings.types;

import io.github.itzispyder.improperui.config.Setting;
import io.github.itzispyder.improperui.config.settings.SettingBuilder;
import io.github.itzispyder.improperui.render.Element;

public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(String name, boolean def, boolean val) {
        super(name, def, val);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Element toGuiElement(int x, int y) {
        return null;
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends SettingBuilder<Boolean, Builder, BooleanSetting> {
        @Override
        public BooleanSetting buildSetting() {
            return new BooleanSetting(name, def, getOrDef(val, def));
        }
    }
}
