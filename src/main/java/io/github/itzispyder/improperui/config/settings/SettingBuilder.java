package io.github.itzispyder.improperui.config.settings;

import io.github.itzispyder.improperui.config.Setting;

public abstract class SettingBuilder<T, B extends SettingBuilder<T, B, S>, S extends Setting<T>> {

    protected SettingChangeCallback<S> changeAction;
    protected String name;
    protected T def, val;

    public SettingBuilder() {
        name = "";
        def = val = null;
        changeAction = setting -> {};
    }

    protected <T> T getOrDef(T val, T def) {
        return val != null ? val : def;
    }

    public B name(String name) {
        this.name = name;
        return (B)this;
    }

    public B def(T def) {
        this.def = def;
        return (B)this;
    }

    public B val(T val) {
        this.val = val;
        return (B)this;
    }

    public B onSettingChange(SettingChangeCallback<S> changeAction) {
        this.changeAction = changeAction;
        return (B)this;
    }

    protected abstract S buildSetting();

    public final S build() {
        S setting = buildSetting();
        setting.setChangeAction((SettingChangeCallback<Setting<T>>)changeAction);
        return setting;
    }
}
