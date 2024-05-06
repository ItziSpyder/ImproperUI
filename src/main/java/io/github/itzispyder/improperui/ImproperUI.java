package io.github.itzispyder.improperui;

import io.github.itzispyder.improperui.client.ImproperUIClient;
import io.github.itzispyder.improperui.config.Paths;
import io.github.itzispyder.improperui.render.Element;
import io.github.itzispyder.improperui.render.ImproperUIPanel;
import io.github.itzispyder.improperui.script.ScriptParser;
import net.fabricmc.api.ModInitializer;

import java.io.File;
import java.util.List;

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
        ImproperUIPanel panel = new ImproperUIPanel();
    }

    public static List<Element> parse(String script) {
        return ScriptParser.parse(script);
    }

    public static List<Element> parse(File file) {
        return ScriptParser.parseFile(file);
    }

    public static void parseAndRun(String script) {
        ScriptParser.run(script);
    }

    public static void parseAndRun(File file) {
        ScriptParser.run(file);
    }
}
