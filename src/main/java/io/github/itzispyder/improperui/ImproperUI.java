package io.github.itzispyder.improperui;

import io.github.itzispyder.improperui.client.ImproperUIClient;
import io.github.itzispyder.improperui.config.Paths;
import net.fabricmc.api.ModInitializer;

public class ImproperUI implements ModInitializer {

    private static boolean initialized = false;

    @Override
    public void onInitialize() {
        ImproperUI.init("improperui");
    }

    public static void init(String modId) {
        if (initialized)
            return;
        initialized = true;
        ImproperUIClient.getInstance().modId = modId;
        Paths.init();
    }
}
