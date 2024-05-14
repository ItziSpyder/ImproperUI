package io.github.itzispyder.improperui;

import io.github.itzispyder.improperui.config.Paths;
import io.github.itzispyder.improperui.render.Element;
import io.github.itzispyder.improperui.render.ImproperUIPanel;
import io.github.itzispyder.improperui.script.CallbackListener;
import io.github.itzispyder.improperui.script.ScriptParser;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImproperUIAPI {

    public static final Logger LOGGER = LoggerFactory.getLogger("ImproperUIAPI");
    private static final Map<String, InitContext> CONTEXTS = new HashMap<>();

    /**
     * Example: ImproperUI.init("improperui", ImproperUI.class, "scripts/example.ui");
     * @param modId YOUR mod's mod ID
     * @param initializer YOUR mod's main initializer, NOT CLIENT INITIALIZER
     * @param scriptPaths Target script files
     */
    public static void init(String modId, Class<? extends ModInitializer> initializer, String... scriptPaths) {
        InitContext context = CONTEXTS.get(modId);

        if (context == null) {
            context = new InitContext(modId, initializer, scriptPaths);
            CONTEXTS.put(modId, context);
        }
        context.init();
    }

    public static void reload() {
        CONTEXTS.values().forEach(InitContext::reload);
    }

    public static List<InitContext> collectContext() {
        return new ArrayList<>(CONTEXTS.values());
    }

    public static InitContext getContext(String modId) {
        return CONTEXTS.get(modId);
    }



    // parse helper methods

    public static List<Element> parse(String script) {
        return ScriptParser.parse(script);
    }

    public static List<Element> parse(File file) {
        return ScriptParser.parseFile(file);
    }

    public static void parseAndRunScript(String script) {
        ScriptParser.run(script);
    }

    /**
     * Parses and runs the script from the path provided
     * @param modId Multiple mods may use ImproperUI at the same time with their own respective scripts, specify the mod ID!
     * @param fileName The file NAME, NOT THE FILE PATH
     */
    public static void parseAndRunFile(String modId, String fileName) {
        ScriptParser.run(new File(Paths.getScripts(modId) + fileName));
    }

    public static void parseAndRunScript(String script, CallbackListener... callbackListeners) {
        ImproperUIPanel panel = new ImproperUIPanel(script, callbackListeners);
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> client.setScreen(panel));
    }

    /**
     * Parses and runs the script from the path provided
     * @param modId Multiple mods may use ImproperUI at the same time with their own respective scripts, specify the mod ID!
     * @param fileName The file NAME, NOT THE FILE PATH
     * @param callbackListeners A list of callbacks that you want to add to the panel screen
     */
    public static void parseAndRunFile(String modId, String fileName, CallbackListener... callbackListeners) {
        File script = new File(Paths.getScripts(modId) + fileName);
        ImproperUIPanel panel = new ImproperUIPanel(script, callbackListeners);
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> client.setScreen(panel));
    }
}
