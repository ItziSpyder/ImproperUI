package io.github.itzispyder.improperui;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ImproperUI implements ModInitializer {

    @Override
    public void onInitialize() {
        ImproperUIAPI.init("improperui", ImproperUI.class,
                "scripts/homescreen.ui",
                "scripts/example.ui"
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (client.options.sneakKey.wasPressed()) {
                ImproperUIAPI.parseAndRunFile("improperui", "homescreen.ui");
            }
        });
    }
}
