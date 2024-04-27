package io.github.itzispyder.improperui.script;

import io.github.itzispyder.improperui.render.Element;
import io.github.itzispyder.improperui.render.Panel;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScriptParser {

    public static void main(String[] args) {
        parseFile(new File("src/main/resources/skibidi.ui"));
    }

    public static void run(File file) {
        var elements = parseFile(file);
        var mc = MinecraftClient.getInstance();

        Panel panel = new Panel();
        elements.forEach(panel::addChild);
        System.out.printf("Created new screen panel with %s children\n", panel.getChildren().size());

        mc.execute(() -> mc.setScreen(panel));
    }

    public static List<Element> parseFile(File file) {
        List<Element> result = new ArrayList<>();

        if (file == null || !file.exists())
            return result;

        String script = ScriptReader.readFile(file.getPath());
        for (String section : ScriptReader.getAllSections(script, '{', '}')) {
            result.add(parseInternal(section));
        }

        return result;
    }

    private static Element parseInternal(String excerpt) {
        Element element = new Element();

        for (String line : ScriptReader.parse(excerpt)) {
            element.callProperty(line);
        }
        for (String section : ScriptReader.getAllSections(excerpt, '{', '}')) {
            element.addChild(parseInternal(section));
        }

        System.out.println(element);
        return element;
    }
}
