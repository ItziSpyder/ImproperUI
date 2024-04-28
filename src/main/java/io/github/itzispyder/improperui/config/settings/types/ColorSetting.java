package io.github.itzispyder.improperui.config.settings.types;

import io.github.itzispyder.improperui.config.Setting;
import io.github.itzispyder.improperui.config.settings.SettingBuilder;
import io.github.itzispyder.improperui.render.Element;
import io.github.itzispyder.improperui.render.math.Color;

public class ColorSetting extends Setting<Color> {

    public ColorSetting(String name, Color val) {
        super(name, val);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Element toGuiElement(int x, int y) {
        return null;
    }

    public static Builder create() {
        return new Builder();
    }


    public static class Builder extends SettingBuilder<Color, Builder, ColorSetting> {
        @Override
        public ColorSetting buildSetting() {
            return new ColorSetting(name, def);
        }
    }
}
