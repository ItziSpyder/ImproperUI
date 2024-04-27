package io.github.itzispyder.improperui.script;

import io.github.itzispyder.improperui.render.Element;
import io.github.itzispyder.improperui.render.Panel;
import io.github.itzispyder.improperui.util.ChatUtils;
import io.github.itzispyder.improperui.util.StringUtils;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScriptParser {

    public static void main(String[] args) {
        parseFile(new File("src/main/resources/skibidi.ui"));
    }

    public static void run(File file) {
        try {
            var elements = parseFile(file);
            var mc = MinecraftClient.getInstance();

            Panel panel = new Panel();
            elements.forEach(panel::addChild);

            mc.execute(() -> mc.setScreen(panel));
        }
        catch (Exception ex) {
            ChatUtils.sendMessage(StringUtils.color("&cError parsing script: " + ex.getMessage()));
        }
    }

    public static List<Element> parseFile(File file) {
        List<Element> result = new ArrayList<>();

        if (file == null || !file.exists())
            return result;

        String script = ScriptReader.readFile(file.getPath());
        for (String section : ScriptReader.getAllSections(script, '{', '}')) {
            result.add(parseInternal(section));
        }
        result.forEach(Element::style);

        return result;
    }

    private static Element parseInternal(String excerpt) {
        Element element = new Element();

        for (String line : ScriptReader.parse(excerpt)) {
            element.queueProperty(line);
        }
        for (String section : ScriptReader.getAllSections(excerpt, '{', '}')) {
            element.addChild(parseInternal(section));
        }

        return element;
    }
}
