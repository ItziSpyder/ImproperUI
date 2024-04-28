package io.github.itzispyder.improperui.script;

import io.github.itzispyder.improperui.render.Element;
import io.github.itzispyder.improperui.render.Panel;
import io.github.itzispyder.improperui.script.callbacks.BuiltInCallbacks;
import io.github.itzispyder.improperui.util.ChatUtils;
import io.github.itzispyder.improperui.util.StringUtils;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScriptParser {

    private static final String ELEMENT = "[a-zA-Z]+(\\s+[#-][a-zA-Z0-9]+)*?\\s*\\{.*\\}";
    private static final String SECTION = "\\s*\\{.*\\}\\s*$";

    public static void main(String[] args) {
        parseFile(new File("src/main/resources/assets/improperui/scripts/skibidi.ui"));
    }

    public static void run(File file) {
        try {
            var elements = parseFile(file);
            var mc = MinecraftClient.getInstance();

            Panel panel = new Panel();
            panel.registerCallback(new BuiltInCallbacks());
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
        for (String section : ScriptReader.parse(script)) {
            result.add(parseInternal(section));
        }
        result.forEach(Element::style);
        result.forEach(Element::printAll);

        return result;
    }

    private static Element parseInternal(String excerpt) {
        Element element = new Element();

        if (excerpt.matches(ELEMENT)) {
            callAttributes(element, excerpt);
            excerpt = ScriptReader.firstSection(excerpt, '{', '}');
        }

        for (String line : ScriptReader.parse(excerpt)) {
            if (!line.matches(ELEMENT)) {
                element.queueProperty(line);
                continue;
            }
            Element child = parseInternal(ScriptReader.firstSection(line, '{', '}'));
            callAttributes(child, line);
            element.addChild(child);
        }

        return element;
    }

    private static void callAttributes(Element element, String scriptLine) {
        String[] attrs = scriptLine.replaceFirst(SECTION, "").split("\s+");
        for (var attr : attrs)
            element.callAttribute(attr);
    }
}