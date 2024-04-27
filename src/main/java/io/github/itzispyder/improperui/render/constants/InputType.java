package io.github.itzispyder.improperui.render.constants;

public enum InputType {

    RELEASE,
    DOWN,
    SCROLL,
    UNKNOWN;

    public boolean isRelease() {
        return this == RELEASE;
    }

    public boolean isDown() {
        return this == DOWN;
    }

    public boolean isScroll() {
        return this == DOWN;
    }

    public boolean isUnknown() {
        return this == UNKNOWN;
    }
}
