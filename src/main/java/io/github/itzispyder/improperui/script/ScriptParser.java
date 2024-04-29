package io.github.itzispyder.improperui.script;

import io.github.itzispyder.improperui.render.Element;
import io.github.itzispyder.improperui.render.Panel;
import io.github.itzispyder.improperui.render.elements.CheckBox;
import io.github.itzispyder.improperui.render.elements.TextField;
import io.github.itzispyder.improperui.script.callbacks.BuiltInCallbacks;
import io.github.itzispyder.improperui.util.ChatUtils;
import io.github.itzispyder.improperui.util.StringUtils;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ScriptParser {

    private static final String ELEMENT = "[a-zA-Z]+(\\s+[#-][a-zA-Z0-9]+)*?\\s*\\{.*\\}";
    private static final String SECTION = "\\s*\\{.*\\}\\s*$";

    private static final Map<String, Supplier<? extends Element>> tagSuppliers = new HashMap<>() {{
        this.put("checkbox", CheckBox::new);
        this.put("textfield", TextField::new);
    }};

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
        if (file == null || !file.exists())
            return new ArrayList<>();

        String script = ScriptReader.readFile(file.getPath());
        return parse(script);
    }

    public static List<Element> parse(String script) {
        List<Element> result = new ArrayList<>();

        script = ScriptReader.condenseLines(script);
        for (String section : ScriptReader.parse(script)) {
            result.add(parseInternal(section));
        }
        for (Element element : result) {
            element.style();
            element.printAll();
        }
        return result;
    }

    private static Element parseInternal(String excerpt) {
        String[] attr = getAttributes(excerpt);
        return parseInternal0(attr[0], excerpt);
    }

    private static Element parseInternal0(String tag, String excerpt) {
        Element element = tagSuppliers.getOrDefault(tag, Element::new).get();

        if (excerpt.matches(ELEMENT)) {
            callAttributes(element, getAttributes(excerpt));
            excerpt = ScriptReader.firstSection(excerpt, '{', '}');
        }

        for (String line : ScriptReader.parse(excerpt)) {
            if (!line.matches(ELEMENT)) {
                element.queueProperty(line);
                continue;
            }
            String[] attr = getAttributes(line);
            Element child = parseInternal0(attr[0], ScriptReader.firstSection(line, '{', '}'));
            callAttributes(child, attr);
            element.addChild(child);
        }

        return element;
    }

    private static String[] getAttributes(String scriptLine) {
        return scriptLine.replaceFirst(SECTION, "").split("\s+");
    }

    private static void callAttributes(Element element, String[] attrs) {
        for (var attr : attrs)
            element.callAttribute(attr);
    }
}