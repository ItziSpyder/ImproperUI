package io.github.itzispyder.improperui;

import net.fabricmc.api.ModInitializer;

public class ImproperUI implements ModInitializer {

    @Override
    public void onInitialize() {
        ImproperUIAPI.init("improperui", ImproperUI.class,
                "scripts/what.ui",
                "scripts/screen.ui"
        );
    }
}
