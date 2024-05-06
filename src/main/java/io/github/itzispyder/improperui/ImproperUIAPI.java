package io.github.itzispyder.improperui;

import io.github.itzispyder.improperui.config.Paths;
import io.github.itzispyder.improperui.render.Element;
import io.github.itzispyder.improperui.render.ImproperUIPanel;
import io.github.itzispyder.improperui.script.CallbackListener;
import io.github.itzispyder.improperui.script.ScriptParser;
import io.github.itzispyder.improperui.util.FileValidationUtils;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImproperUIAPI {

    private static boolean initialized = false;
    private static final Set<String> paths = new HashSet<>();

    /**
     * Example: ImproperUI.init("improperui", ImproperUI.class, "scripts/example.ui");
     * @param modId YOUR mod's mod ID
     * @param initializer YOUR mod's main initializer, NOT CLIENT INITIALIZER
     * @param scriptPaths Target script files
     */
    public static void init(String modId, Class<? extends ModInitializer> initializer, String... scriptPaths) {
        if (initialized)
            return;
        ImproperUIAPI.initialized = true;
        ImproperUIAPI.paths.clear();
        Paths.init();

        var loader = initializer.getClassLoader();
        for (String path : scriptPaths) {
            copyResource(modId, loader, path);
        }
    }

    public static void reload(String modId, Class<? extends ModInitializer> initializer, String... scriptPaths) {
        reInit(modId, initializer, scriptPaths);
    }

    public static void reInit(String modId, Class<? extends ModInitializer> initializer, String... scriptPaths) {
        initialized = false;
        init(modId, initializer, scriptPaths);
    }

    private static void copyResource(String modId, ClassLoader loader, String path) {
        try {
            String name = path.trim().replaceAll(".*/", "");
            if (paths.contains(name))
                return;

            InputStream is = loader.getResourceAsStream(path);

            if (is == null)
                throw new IllegalArgumentException("resource not found!");

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String read = String.join("\n", br.lines().toList());
            br.close();
            isr.close();
            is.close();

            File file = new File(Paths.getScripts(modId) + name);
            FileValidationUtils.validate(file);

            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(read);
            bw.close();
            fw.close();

            paths.add(name);
        }
        catch (Exception ex) {
            System.err.printf("Error copying resource '%s': %s\n", path, ex.getMessage());
        }
    }

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
