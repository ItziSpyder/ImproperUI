package io.github.itzispyder.improperui.client;

import io.github.itzispyder.improperui.config.keybinds.Keybind;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.font.TextRenderer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImproperUIClient implements ClientModInitializer {

    private static final ImproperUIClient system = new ImproperUIClient();
    public static ImproperUIClient getInstance() {
        return system;
    }

    public TextRenderer codeRenderer;
    private final Set<Keybind> keybinds;

    public ImproperUIClient() {
        this.keybinds = new HashSet<>();
    }

    @Override
    public void onInitializeClient() {

    }

    public void addKeybind(Keybind bind) {
        if (bind == null) return;
        this.keybinds.add(bind);
    }

    public void removeKeybind(Keybind bind) {
        this.keybinds.remove(bind);
    }

    public Set<Keybind> keybinds() {
        return new HashSet<>(keybinds);
    }

    public List<Keybind> getBindsOf(int key) {
        return keybinds().stream().filter(bind -> bind.getKey() == key).toList();
    }
}
