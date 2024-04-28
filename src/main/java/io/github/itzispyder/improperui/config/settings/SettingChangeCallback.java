package io.github.itzispyder.improperui.config.settings;

import io.github.itzispyder.improperui.config.Setting;

@FunctionalInterface
public interface SettingChangeCallback<T extends Setting<?>> {

    void onChange(T setting);
}
