package io.github.itzispyder.improperui.render.elements;

import io.github.itzispyder.improperui.render.Element;

public class Button extends Element {

    public Button() {
        super();
        queueProperty("size: 69 7");
        queueProperty("inner-text: \"Button\"");
        queueProperty("background-color: white");
        queueProperty("border-radius: 2");
        queueProperty("padding: 2");
        queueProperty("margin: 2");
        queueProperty("text-align: center");
        queueProperty("text-color: dark_gray");
        queueProperty("hovered => { border-thickness: 1; border-color: dark_gray; }");
        queueProperty("selected => { border-thickness: 0; background-color: gray; text-color: light_gray; }");
    }
}
