package io.github.itzispyder.improperui.render.elements;

import io.github.itzispyder.improperui.config.ConfigKey;
import io.github.itzispyder.improperui.config.PropertyCache;
import io.github.itzispyder.improperui.render.KeyHolderElement;

public class Positionable extends KeyHolderElement {

    public Positionable() {
        super();
        queueProperty("size: 100 200");
        queueProperty("border-radius: 5");
        queueProperty("shadow-distance: 5");
        queueProperty("background-color: #80FFFFFF");
        queueProperty("draggable: true");
    }

    @Override
    public void style() {
        if (getId() == null)
            throw new IllegalStateException("a Positionable element cannot have a null ID!");
        if (super.getConfigKey() == null)
            throw new IllegalStateException("a Positionable element needs to have a ConfigKey attribute \"-modid:config.properties:keyname\"");
        super.style();
    }

    @Override
    public void onLoadKey(PropertyCache cache, ConfigKey key) {
        var property = cache.getProperty(key);
        if (property != null)
            moveTo(property.get(0).toInt(), property.get(1).toInt());
    }

    @Override
    public void onSaveKey(PropertyCache cache, ConfigKey key) {
        cache.setProperty(key, "%s %s".formatted(x, y), true);
    }

    @Override
    public ConfigKey getConfigKey() {
        var key = super.getConfigKey();
        return new ConfigKey(key.modId, key.path, "improperui.elements.positionable.%s.%s".formatted(getId(), key.key));
    }
}
