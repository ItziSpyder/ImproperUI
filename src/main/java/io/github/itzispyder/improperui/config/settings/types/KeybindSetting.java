package io.github.itzispyder.improperui.config.settings.types;

import io.github.itzispyder.improperui.config.Setting;
import io.github.itzispyder.improperui.config.keybinds.BindCondition;
import io.github.itzispyder.improperui.config.keybinds.KeyAction;
import io.github.itzispyder.improperui.config.keybinds.Keybind;
import io.github.itzispyder.improperui.config.settings.SettingBuilder;
import io.github.itzispyder.improperui.render.Element;

public class KeybindSetting extends Setting<Keybind> {

    public final Keybind bind;

    public KeybindSetting(String name, Keybind bind) {
        super(name, bind);
        this.bind = bind;
    }

    public int getKey() {
        return bind.getKey();
    }

    public void setKey(int val) {
        bind.setKey(val);
    }

    public int getDefKey() {
        return bind.getDefaultKey();
    }

    public void setDefKey(int def) {
        bind.setDefaultKey(def);
    }

    public Keybind getBind() {
        return bind;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Element toGuiElement(int x, int y) {
        return null;
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends SettingBuilder<Keybind, Builder, KeybindSetting> {

        private String id;
        private int key, defaultKey;
        private KeyAction keyAction, releaseAction;
        private BindCondition bindCondition;

        public Builder() {
            id = "unregistered-keybind";
            key = defaultKey = Keybind.NONE;
            keyAction = bind -> {};
            bindCondition = (bind, screen) -> true;
        }

        public Builder val(int key) {
            this.key = key;
            return this;
        }

        public Builder def(int defaultKey) {
            this.defaultKey = defaultKey;
            return this;
        }

        @Override
        public Builder name(String id) {
            this.id = id;
            return this;
        }

        public Builder onPress(KeyAction keyAction) {
            this.keyAction = keyAction;
            return this;
        }

        public Builder onRelease(KeyAction releaseAction) {
            this.releaseAction = releaseAction;
            return this;
        }

        public Builder condition(BindCondition bindCondition) {
            this.bindCondition = bindCondition;
            return this;
        }

        @Override
        public KeybindSetting buildSetting() {
            key = key == Keybind.NONE ? defaultKey : key;
            return new KeybindSetting(id, Keybind.create()
                    .id(id)
                    .key(key)
                    .defaultKey(defaultKey)
                    .onPress(keyAction)
                    .onRelease(releaseAction)
                    .condition(bindCondition)
                    .build()
            );
        }
    }
}
