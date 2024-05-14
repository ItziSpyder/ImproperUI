package io.github.itzispyder.improperui;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ImproperUI implements ModInitializer {

    public static final KeyBinding BIND = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "binds.improperui.menu",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "binds.improperui"
    ));

    @Override
    public void onInitialize() {
        ImproperUIAPI.init("improperui", ImproperUI.class,
                "scripts/homescreen.ui",
                "scripts/example.ui"
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (BIND.wasPressed()) {
                ImproperUIAPI.parseAndRunFile("improperui", "homescreen.ui");
            }
        });
    }
}
