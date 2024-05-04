package io.github.itzispyder.improperui.render;

import io.github.itzispyder.improperui.config.ConfigKey;
import io.github.itzispyder.improperui.config.ConfigKeyHolder;
import io.github.itzispyder.improperui.config.PropertyCache;
import io.github.itzispyder.improperui.script.ScriptParser;

public abstract class KeyHolderElement extends Element implements ConfigKeyHolder {

    public abstract void onLoadKey(PropertyCache cache, ConfigKey key);
    public abstract void onSaveKey(PropertyCache cache, ConfigKey key);

    protected KeyHolderElement(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    protected KeyHolderElement() {
        this(0, 0, 0, 0);
    }

    @Override
    public void style() {
        super.style();

        var key = getConfigKey();
        if (key != null)
            onLoadKey(ScriptParser.CACHE, key);
    }

    @Override
    public void onLeftClick(int mx, int my, boolean release) {
        super.onLeftClick(mx, my, release);

        var key = getConfigKey();
        if (key != null && release)
            onSaveKey(ScriptParser.CACHE, key);
    }

    @Override
    public ConfigKey getConfigKey() {
        return ELEMENT_KEY_HOLDER.apply(this);
    }
}
