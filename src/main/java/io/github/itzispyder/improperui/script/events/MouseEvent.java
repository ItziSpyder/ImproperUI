package io.github.itzispyder.improperui.script.events;

import io.github.itzispyder.improperui.render.Element;
import io.github.itzispyder.improperui.render.constants.InputType;
import io.github.itzispyder.improperui.script.Event;

public class MouseEvent extends Event {

    public final int button;
    public final int delta;
    public final InputType input;
    public final Element target;

    public MouseEvent(int button, int delta, InputType input, Element target) {
        this.button = button;
        this.delta = delta;
        this.input = input;
        this.target = target;
    }
}
