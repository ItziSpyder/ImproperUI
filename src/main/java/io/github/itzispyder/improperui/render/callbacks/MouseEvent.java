package io.github.itzispyder.improperui.render.callbacks;

import io.github.itzispyder.improperui.render.constants.InputType;

public record MouseEvent(int button, InputType action) {

    @FunctionalInterface
    public interface Listener {
        void onKey(MouseEvent e);
    }
}
