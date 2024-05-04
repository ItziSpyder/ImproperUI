package io.github.itzispyder.improperui.render.elements;

import io.github.itzispyder.improperui.config.ConfigKey;
import io.github.itzispyder.improperui.config.PropertyCache;
import io.github.itzispyder.improperui.render.KeyHolderElement;

public class CheckBox extends KeyHolderElement {

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

    @Override
    public void onLeftClick(int mx, int my, boolean release) {
        super.onLeftClick(mx, my, release);
        if (!release)
            setActive(!isActive());
    }

    @Override
    public void onLoadKey(PropertyCache cache, ConfigKey key) {
        var property = cache.getProperty(key);
        if (property != null)
            setActive(property.get(0).toBool());
    }

    @Override
    public void onSaveKey(PropertyCache cache, ConfigKey key) {
        cache.setProperty(key, isActive(), true);
    }
}
