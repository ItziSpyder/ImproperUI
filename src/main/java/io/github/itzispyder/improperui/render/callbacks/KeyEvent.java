package io.github.itzispyder.improperui.render.callbacks;

import io.github.itzispyder.improperui.render.constants.InputType;

public record KeyEvent(int key, int scan, InputType action) {

    @FunctionalInterface
    public interface Listener {
        void onKey(int key, int scan, InputType action);
    }
}
