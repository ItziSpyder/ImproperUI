package io.github.itzispyder.improperui.render.elements;

import io.github.itzispyder.improperui.render.Element;

public class CheckBox extends Element {

    public CheckBox() {
        super();
        queueProperty("text-align: center");
        queueProperty("size: 10");
        queueProperty("border: 1 0 white");
        queueProperty("background-color: black");
    }

    @Override
    public void init() {
        super.init();
        registerProperty("active", args -> setActive(args.get(0).toBool()));
    }

    @Override
    public void onLeftClick(boolean release) {
        super.onLeftClick(release);
        if (!release)
            setActive(!isActive());
    }

    public boolean isActive() {
        return classList.contains("active");
    }

    public void setActive(boolean active) {
        if (active) {
            classList.add("active");
            innerText = "✔";
        }
        else {
            classList.remove("active");
            innerText = "";
        }
    }
}
