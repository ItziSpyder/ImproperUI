package io.github.itzispyder.improperui.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.font.TextRenderer;

public class ImproperUIClient implements ClientModInitializer {

    private static final ImproperUIClient system = new ImproperUIClient();
    public static ImproperUIClient getInstance() {
        return system;
    }

    public TextRenderer codeRenderer;

    public ImproperUIClient() {

    }

    @Override
    public void onInitializeClient() {

    }
}
