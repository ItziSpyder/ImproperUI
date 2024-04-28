package io.github.itzispyder.improperui.config.keybinds;

@FunctionalInterface
public interface KeyAction {

    void onKey(Keybind bind);
}
