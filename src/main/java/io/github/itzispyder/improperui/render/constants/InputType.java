package io.github.itzispyder.improperui.render.constants;

public enum InputType {

    RELEASE,
    CLICK,
    HOLD,
    SCROLL,
    UNKNOWN;

    public static InputType of(int action) {
        InputType r;
        switch (action) {
            case 0 -> r = RELEASE;
            case 1 -> r = CLICK;
            case 2 -> r = HOLD;
            default -> r = UNKNOWN;
        }
        return r;
    }

    public boolean isRelease() {
        return this == RELEASE;
    }

    public boolean isClick() {
        return this == CLICK;
    }

    public boolean isHold() {
        return this == HOLD;
    }

    public boolean isUnknown() {
        return this == UNKNOWN;
    }

    public boolean isUp() {
        return this == RELEASE || this == UNKNOWN;
    }

    public boolean isDown() {
        return this == CLICK || this == HOLD;
    }
}
