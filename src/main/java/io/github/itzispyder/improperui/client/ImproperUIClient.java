package io.github.itzispyder.improperui.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.Font;

public class ImproperUIClient implements ClientModInitializer {

    private static final ImproperUIClient system = new ImproperUIClient();
    public static ImproperUIClient getInstance() {
        return system;
    }

    public Font codeRenderer;

    public ImproperUIClient() {

    }

    @Override
    public void onInitializeClient() {

    }
}
