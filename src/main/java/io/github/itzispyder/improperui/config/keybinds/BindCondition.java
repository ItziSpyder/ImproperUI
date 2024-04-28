package io.github.itzispyder.improperui.config.keybinds;

import net.minecraft.client.gui.screen.Screen;

@FunctionalInterface
public interface BindCondition {

    boolean meets(Keybind bind, Screen screen);
}
