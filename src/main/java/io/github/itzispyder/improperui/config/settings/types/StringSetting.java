package io.github.itzispyder.improperui.config.settings.types;

import io.github.itzispyder.improperui.config.Setting;
import io.github.itzispyder.improperui.config.settings.SettingBuilder;
import io.github.itzispyder.improperui.render.Element;

public class StringSetting extends Setting<String> {

    public StringSetting(String name, String val) {
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

    public static class Builder extends SettingBuilder<String, Builder, StringSetting> {
        @Override
        protected StringSetting buildSetting() {
            return new StringSetting(name, def);
        }
    }
}
