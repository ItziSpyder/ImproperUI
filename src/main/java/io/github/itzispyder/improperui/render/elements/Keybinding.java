package io.github.itzispyder.improperui.render.elements;

import io.github.itzispyder.improperui.config.ConfigKey;
import io.github.itzispyder.improperui.config.PropertyCache;
import io.github.itzispyder.improperui.config.keybinds.Keybind;
import io.github.itzispyder.improperui.render.KeyHolderElement;
import io.github.itzispyder.improperui.script.ScriptParser;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

public class Keybinding extends KeyHolderElement {

    private int currentScanCode, currentKeyCode;

    public Keybinding() {
        super();
        this.currentScanCode = 42;

        queueProperty("size: 69 7");
        queueProperty("background-color: white");
        queueProperty("text-color: dark_gray");
        queueProperty("text-align: center");
        queueProperty("border-radius: 2");
        queueProperty("padding: 2");
        queueProperty("margin: 2");
        queueProperty("focused => { border-thickness: 1; border-color: white; text-color: gray; inner-text: \"[press key]\"; }");
        queueProperty("selected => { border-thickness: 0; background-color: gray; text-color: white; }");
    }

    @Override
    public void onKey(int key, int scan, boolean release) {
        if (parentPanel != null) {
            currentKeyCode = key == GLFW.GLFW_KEY_ESCAPE ? Keybind.NONE : key;
            currentScanCode = scan;
            parentPanel.focused = null;
            onSaveKey(ScriptParser.CACHE, getConfigKey());
        }
        super.onKey(key, scan, release);
    }

    @Override
    public void onRender(DrawContext context, int mx, int my, float delta) {
        updateDisplay();
        super.onRender(context, mx, my, delta);
    }

    @Override
    public void onLoadKey(PropertyCache cache, ConfigKey key) {
        var property = cache.getProperty(key);
        if (property != null)
            currentKeyCode = property.get(0).toInt();
    }

    @Override
    public void onSaveKey(PropertyCache cache, ConfigKey key) {
        cache.setProperty(key, currentKeyCode, true);
    }

    public void updateDisplay() {
        String name = GLFW.glfwGetKeyName(currentKeyCode, currentScanCode);

        if (name == null || Keybind.EXTRAS.containsKey(currentKeyCode)) {
            name = Keybind.EXTRAS.get(currentKeyCode);
        }

        name = name != null && currentKeyCode != Keybind.NONE ? (name.length() == 1 ? name.toUpperCase() : name) : "NA";
        innerText = name;
    }
}
