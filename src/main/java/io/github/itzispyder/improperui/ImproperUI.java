package io.github.itzispyder.improperui;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.itzispyder.improperui.script.callbacks.BuiltInCallbacks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class ImproperUI implements ModInitializer {

    public static final KeyMapping BIND = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "binds.improperui.menu",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("improperui", "binds.improperui"))
    ));

    @Override
    public void onInitialize() {
        ImproperUIAPI.init("improperui", ImproperUI.class,
                "assets/improperui/improperui/homescreen.ui",
                "assets/improperui/improperui/example.ui"
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (BIND.consumeClick()) {
                ImproperUIAPI.parseAndRunFile("improperui", "homescreen.ui", new BuiltInCallbacks());
            }
        });
    }
}
