package io.github.itzispyder.improperui.render.elements;

public class Header extends Label {

    public Header(float textScale) {
        super();
        queueProperty("inner-text-prefix: \"&l\"");
        queueProperty("text-scale: " + textScale);
    }
}
